from pathlib import Path
import zipfile
import shutil

PROJECT = Path.home() / "Microplastic_Project"

RAW = PROJECT / "dataset_raw" / "dataset_microplastic-main"
ANNOTATIONS = PROJECT / "annotation_raw" / "Alldataset_annotation"
OUTPUT = PROJECT / "dataset_work"

CLASSES = [
    "ABS",
    "Nylon",
    "PE",
    "PET",
    "PS",
    "PVC",
]


def build_zip_index():

    print("\nBuilding image index from ZIP files...")

    image_index = {}

    for zip_path in RAW.glob("*.zip"):

        if zip_path.name == "Alldataset_annotation.zip":
            continue

        print(f"Scanning: {zip_path.name}")

        with zipfile.ZipFile(zip_path) as z:

            for name in z.namelist():

                if not name.lower().endswith((".jpg", ".jpeg", ".png")):
                    continue

                filename = Path(name).name

                if filename in image_index:

                    image_index[filename].append(zip_path)

                else:

                    image_index[filename] = [zip_path]

    return image_index


def build_dataset(image_index):

    totals = {}

    for cls in CLASSES:

        annotation_dir = ANNOTATIONS / cls

        image_output = OUTPUT / "images_all" / cls
        label_output = OUTPUT / "labels_all" / cls

        image_output.mkdir(parents=True, exist_ok=True)
        label_output.mkdir(parents=True, exist_ok=True)

        annotations = sorted(annotation_dir.glob("*.txt"))

        totals[cls] = {
            "labels": len(annotations),
            "images": 0,
        }

        print(f"\n========== {cls} ==========")
        print(f"Annotations: {len(annotations)}")

        for label in annotations:

            filename = label.stem + ".jpg"

            if filename not in image_index:

                # Try PNG/JPEG alternatives
                alternatives = [
                    label.stem + ".jpeg",
                    label.stem + ".png",
                ]

                found = None

                for alt in alternatives:

                    if alt in image_index:
                        found = alt
                        break

                if found is None:

                    print(f"ERROR: Image not found: {filename}")

                    continue

                filename = found

            locations = image_index[filename]

            if len(locations) > 1:

                print(
                    f"WARNING: Duplicate image name: "
                    f"{filename}"
                )

                for location in locations:
                    print(f"  {location.name}")

                raise RuntimeError(
                    f"Duplicate image filename: {filename}"
                )

            zip_path = locations[0]

            # Extract image
            with zipfile.ZipFile(zip_path) as z:

                matching_member = None

                for member in z.namelist():

                    if Path(member).name == filename:

                        matching_member = member
                        break

                if matching_member is None:

                    raise RuntimeError(
                        f"Could not locate {filename} "
                        f"inside {zip_path.name}"
                    )

                destination = image_output / filename

                with z.open(matching_member) as source:
                    with open(destination, "wb") as target:
                        shutil.copyfileobj(source, target)

            # Copy annotation
            shutil.copy2(
                label,
                label_output / label.name
            )

            totals[cls]["images"] += 1

    return totals


def main():

    print("=" * 60)
    print("BUILDING CLEAN MICROPLASTIC DATASET")
    print("=" * 60)

    image_index = build_zip_index()

    print(f"\nUnique image filenames indexed: {len(image_index)}")

    totals = build_dataset(image_index)

    print("\n" + "=" * 60)
    print("FINAL RESULT")
    print("=" * 60)

    total_images = 0
    total_labels = 0

    for cls in CLASSES:

        images = totals[cls]["images"]
        labels = totals[cls]["labels"]

        total_images += images
        total_labels += labels

        status = "✓" if images == labels else "✗"

        print(
            f"{status} {cls:8} "
            f"Images: {images:4} "
            f"Labels: {labels:4}"
        )

    print("-" * 60)

    print(f"Images: {total_images}")
    print(f"Labels: {total_labels}")

    if total_images == total_labels:

        print("\n✓ CLEAN DATASET CREATED")

    else:

        print("\n✗ DATASET STILL HAS MISMATCHES")

    print("=" * 60)


if __name__ == "__main__":
    main()
