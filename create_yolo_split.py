from pathlib import Path
import random
import shutil

PROJECT = Path.home() / "Microplastic_Project"

SOURCE_IMAGES = PROJECT / "dataset_work" / "images_all"
SOURCE_LABELS = PROJECT / "dataset_work" / "labels_all"

OUTPUT = PROJECT / "microplastic_yolo"

CLASSES = [
    "ABS",
    "Nylon",
    "PE",
    "PET",
    "PS",
    "PVC",
]

TRAIN_RATIO = 0.70
VAL_RATIO = 0.20
TEST_RATIO = 0.10

SEED = 42


def main():

    random.seed(SEED)

    # Create output directories
    for split in ["train", "val", "test"]:
        (OUTPUT / "images" / split).mkdir(
            parents=True,
            exist_ok=True
        )

        (OUTPUT / "labels" / split).mkdir(
            parents=True,
            exist_ok=True
        )

    total_counts = {
        "train": 0,
        "val": 0,
        "test": 0
    }

    print("=" * 60)
    print("CREATING STRATIFIED YOLO DATASET")
    print("=" * 60)

    for class_name in CLASSES:

        image_dir = SOURCE_IMAGES / class_name
        label_dir = SOURCE_LABELS / class_name

        images = sorted(image_dir.glob("*.jpg"))

        pairs = []

        for image in images:

            label = label_dir / f"{image.stem}.txt"

            if not label.exists():
                raise FileNotFoundError(
                    f"Missing label:\n{label}"
                )

            pairs.append((image, label))

        random.shuffle(pairs)

        total = len(pairs)

        train_count = int(total * TRAIN_RATIO)
        val_count = int(total * VAL_RATIO)

        train_pairs = pairs[:train_count]

        val_pairs = pairs[
            train_count:
            train_count + val_count
        ]

        test_pairs = pairs[
            train_count + val_count:
        ]

        splits = {
            "train": train_pairs,
            "val": val_pairs,
            "test": test_pairs
        }

        print(f"\n{class_name}")
        print(f"  Total : {total}")
        print(f"  Train : {len(train_pairs)}")
        print(f"  Val   : {len(val_pairs)}")
        print(f"  Test  : {len(test_pairs)}")

        for split, split_pairs in splits.items():

            for image, label in split_pairs:

                shutil.copy2(
                    image,
                    OUTPUT / "images" / split / image.name
                )

                shutil.copy2(
                    label,
                    OUTPUT / "labels" / split / label.name
                )

            total_counts[split] += len(split_pairs)

    print("\n" + "=" * 60)
    print("FINAL SPLIT")
    print("=" * 60)

    print(f"Train : {total_counts['train']}")
    print(f"Val   : {total_counts['val']}")
    print(f"Test  : {total_counts['test']}")
    print(
        f"Total : "
        f"{sum(total_counts.values())}"
    )

    print("=" * 60)


if __name__ == "__main__":
    main()
