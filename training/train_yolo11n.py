from ultralytics import YOLO
from pathlib import Path
import argparse

PROJECT_ROOT = Path(__file__).resolve().parent.parent
DATA_YAML = PROJECT_ROOT / "microplastic_yolo" / "data.yaml"
RUNS_DIR = PROJECT_ROOT / "runs"

def train(args):
    print("=" * 60)
    print("MICROPLASTIC DETECTION - YOLO11n")
    print("=" * 60)
    print(f"Dataset : {DATA_YAML}")
    print(f"Model   : YOLO11n")
    print(f"Epochs  : {args.epochs}")
    print(f"Image   : {args.imgsz}")
    print(f"Batch   : {args.batch}")
    print(f"Device  : {args.device}")
    print(f"Workers : {args.workers}")
    print("=" * 60)

    model = YOLO("yolo11n.pt")

    model.train(
        data=str(DATA_YAML),
        epochs=args.epochs,
        imgsz=args.imgsz,
        batch=args.batch,
        device=args.device,
        workers=args.workers,
        project=str(RUNS_DIR),
        name=args.name,
        exist_ok=True,
        seed=42,
        save=True,
        val=True,
        cache=False,
        verbose=True,
    )

    print("\n" + "=" * 60)
    print("YOLO11n TRAINING COMPLETED")
    print("=" * 60)
    print(f"Results: {RUNS_DIR / args.name}")
    print("=" * 60)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Train YOLO11n for microplastic detection"
    )

    parser.add_argument("--epochs", type=int, default=50)
    parser.add_argument("--imgsz", type=int, default=640)
    parser.add_argument("--batch", type=int, default=8)
    parser.add_argument("--device", type=str, default="cpu")
    parser.add_argument("--workers", type=int, default=2)
    parser.add_argument("--name", type=str, default="yolo11n_training")

    args = parser.parse_args()
    train(args)
