from pathlib import Path
import random

import cv2
import matplotlib.pyplot as plt


PROJECT = Path.home() / "Microplastic_Project"

IMAGE_DIR = PROJECT / "dataset_work" / "images_all"
LABEL_DIR = PROJECT / "dataset_work" / "labels_all"

CLASSES = {
    0: "ABS",
    1: "Nylon",
    2: "PE",
    3: "PET",
    4: "PS",
    5: "PVC",
}

random.seed(42)


def draw_boxes(image, label_file):

    height, width = image.shape[:2]

    with open(label_file, "r") as f:

        for line in f:

            parts = line.strip().split()

            if len(parts) != 5:
                continue

            class_id = int(parts[0])

            x_center = float(parts[1])
            y_center = float(parts[2])
            box_width = float(parts[3])
            box_height = float(parts[4])

            x1 = int((x_center - box_width / 2) * width)
            y1 = int((y_center - box_height / 2) * height)

            x2 = int((x_center + box_width / 2) * width)
            y2 = int((y_center + box_height / 2) * height)

            cv2.rectangle(
                image,
                (x1, y1),
                (x2, y2),
                (255, 0, 0),
                2
            )

            label = CLASSES.get(class_id, f"Unknown:{class_id}")

            cv2.putText(
                image,
                label,
                (x1, max(y1 - 5, 15)),
                cv2.FONT_HERSHEY_SIMPLEX,
                0.5,
                (255, 0, 0),
                1,
                cv2.LINE_AA
            )

    return image


def main():

    for class_name in CLASSES.values():

        image_folder = IMAGE_DIR / class_name
        label_folder = LABEL_DIR / class_name

        images = list(image_folder.glob("*.jpg"))

        if not images:
            print(f"No images found for {class_name}")
            continue

        # Show 3 random samples from each class
        samples = random.sample(
            images,
            min(3, len(images))
        )

        for image_path in samples:

            label_path = label_folder / f"{image_path.stem}.txt"

            if not label_path.exists():

                print(
                    f"Missing label: {image_path.name}"
                )

                continue

            image = cv2.imread(str(image_path))

            if image is None:
                print(
                    f"Could not read: {image_path}"
                )
                continue

            image = cv2.cvtColor(
                image,
                cv2.COLOR_BGR2RGB
            )

            image = draw_boxes(
                image,
                label_path
            )

            plt.figure(figsize=(10, 8))

            plt.imshow(image)

            plt.title(
                f"{class_name} - {image_path.name}"
            )

            plt.axis("off")

            plt.show()


if __name__ == "__main__":
    main()
