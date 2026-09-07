from pathlib import Path
import zipfile

PROJECT = Path.home() / "Microplastic_Project"

RAW = PROJECT / "dataset_raw" / "dataset_microplastic-main"
ANNOTATIONS = PROJECT / "annotation_raw" / "Alldataset_annotation"

CLASSES = {
    "ABS": 0,
    "Nylon": 1,
    "PE": 2,
    "PET": 3,
    "PS": 4,
    "PVC": 5,
}


def get_zip_images():

    images = {cls: set() for cls in CLASSES}

    for zip_path in RAW.glob("*.zip"):

        if zip_path.name == "Alldataset_annotation.zip":
            continue

        # Determine class from ZIP filename
        cls = None

        for class_name in CLASSES:

            if zip_path.name.startswith(class_name):
                cls = class_name
                break

        if cls is None:
            continue

        with zipfile.ZipFile(zip_path) as z:

            for name in z.namelist():

                # We only care about JPG images
                if not name.lower().endswith(".jpg"):
                    continue

                filename = Path(name).name

                # IMPORTANT:
                # Do NOT require filename to start with class name.
                #
                # Some files are:
                # ABS (1).jpg
                #
                # Others are:
                # 20250224-170002-232.jpg

                images[cls].add(filename)

    return images


def get_annotations():

    labels = {cls: set() for cls in CLASSES}

    for cls in CLASSES:

        folder = ANNOTATIONS / cls

        if not folder.exists():
            print(f"WARNING: annotation folder missing: {folder}")
            continue

        for txt in folder.glob("*.txt"):
            labels[cls].add(txt.name)

    return labels


def validate_yolo_labels():

    errors = []

    for cls, expected_id in CLASSES.items():

        folder = ANNOTATIONS / cls

        for txt in folder.glob("*.txt"):

            try:

                lines = txt.read_text().strip().splitlines()

                for line_no, line in enumerate(lines, 1):

                    if not line.strip():
                        continue

                    parts = line.split()

                    if len(parts) != 5:

                        errors.append(
                            f"{txt}: line {line_no}: "
                            f"expected 5 values, got {len(parts)}"
                        )

                        continue

                    class_id = int(parts[0])

                    values = list(map(float, parts[1:]))

                    # Check class ID
                    if class_id != expected_id:

                        errors.append(
                            f"{txt}: line {line_no}: "
                            f"expected class {expected_id}, "
                            f"found {class_id}"
                        )

                    # Check normalized coordinates
                    for value in values:

                        if not 0 <= value <= 1:

                            errors.append(
                                f"{txt}: line {line_no}: "
                                f"coordinate {value} outside [0,1]"
                            )

            except Exception as e:

                errors.append(f"{txt}: {e}")

    return errors


def main():

    print("=" * 60)
    print("MICROPLASTIC DATASET VALIDATION")
    print("=" * 60)

    images = get_zip_images()
    labels = get_annotations()

    total_images = 0
    total_labels = 0

    print("\nIMAGE / ANNOTATION MATCHING\n")

    for cls in CLASSES:

        image_names = images[cls]

        # Convert:
        # filename.txt
        #
        # into:
        # filename.jpg

        label_names = {
            Path(name).stem + ".jpg"
            for name in labels[cls]
        }

        missing_labels = image_names - label_names
        missing_images = label_names - image_names

        print(f"{cls:8}")

        print(f"  Images:          {len(image_names)}")
        print(f"  Labels:          {len(labels[cls])}")
        print(f"  Missing labels:  {len(missing_labels)}")
        print(f"  Missing images:  {len(missing_images)}")

        if missing_labels:

            print("  Example missing labels:")

            for x in sorted(missing_labels)[:5]:
                print(f"    {x}")

        if missing_images:

            print("  Example missing images:")

            for x in sorted(missing_images)[:5]:
                print(f"    {x}")

        total_images += len(image_names)
        total_labels += len(labels[cls])

    print("\n" + "=" * 60)

    print(f"TOTAL IMAGES: {total_images}")
    print(f"TOTAL LABELS: {total_labels}")

    print("=" * 60)

    print("\nYOLO LABEL VALIDATION\n")

    errors = validate_yolo_labels()

    if not errors:

        print("✓ All YOLO annotation files passed validation.")

    else:

        print(f"✗ Found {len(errors)} annotation errors.")

        for error in errors[:30]:
            print(" ", error)

        if len(errors) > 30:
            print(
                f"\n... and {len(errors) - 30} more."
            )

    print("\n" + "=" * 60)

    if total_images == total_labels and not errors:

        print("✓ DATASET PASSED BASIC VALIDATION")

    else:

        print("✗ DATASET NEEDS INVESTIGATION")

    print("=" * 60)


if __name__ == "__main__":
    main()
