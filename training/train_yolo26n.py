from ultralytics import YOLO
from pathlib import Path
import argparse

PROJECT_ROOT = Path(__file__).resolve().parent.parent
DATA_YAML = PROJECT_ROOT / "microplastic_yolo" / "data.yaml"
RUNS_DIR = PROJECT_ROOT / "runs"

def train(args):
    print("=" * 60)
    print("MICROPLASTIC DETECTION - YOLO26n")
    print("=" * 60)
    print(f"Dataset      : {DATA_YAML}")
    print(f"Model        : YOLO26n")
    print(f"Epochs       : {args.epochs}")
    print(f"Image        : {args.imgsz}")
    print(f"Batch        : {args.batch}")
    print(f"Learning Rate: {args.lr0}")
    print(f"Optimizer    : {args.optimizer}")
    print(f"Momentum     : {args.momentum}")
    print(f"Weight Decay : {args.weight_decay}")
    print(f"Device       : {args.device}")
    print(f"Workers      : {args.workers}")
    print("=" * 60)

    model_path = PROJECT_ROOT / "models" / "yolo26n.pt"
    if not model_path.exists():
        model_path = PROJECT_ROOT / "weights" / "yolo26n.pt"

    model = YOLO(str(model_path))

    model.train(
        data=str(DATA_YAML),
        epochs=args.epochs,
        imgsz=args.imgsz,
        batch=args.batch,
        lr0=args.lr0,
        optimizer=args.optimizer,
        momentum=args.momentum,
        weight_decay=args.weight_decay,
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
    print("YOLO26n TRAINING COMPLETED")
    print("=" * 60)
    print(f"Results: {RUNS_DIR / args.name}")
    print("=" * 60)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Train YOLO26n for microplastic detection"
    )

    parser.add_argument("--epochs", type=int, default=50)
    parser.add_argument("--imgsz", type=int, default=640)
    parser.add_argument("--batch", type=int, default=16)
    parser.add_argument("--lr0", type=float, default=0.001)
    parser.add_argument("--optimizer", type=str, default="AdamW")
    parser.add_argument("--momentum", type=float, default=0.9)
    parser.add_argument("--weight-decay", dest="weight_decay", type=float, default=0.0005)
    parser.add_argument("--device", type=str, default="0")
    parser.add_argument("--workers", type=int, default=2)
    parser.add_argument("--name", type=str, default="yolo26n_training")

    args = parser.parse_args()
    train(args)
