import subprocess
import sys
from pathlib import Path

TRAINING_DIR = Path(__file__).resolve().parent

models = [
    ("YOLO11n", TRAINING_DIR / "train_yolo11n.py", "yolo11n_training"),
    ("YOLO26n", TRAINING_DIR / "train_yolo26n.py", "yolo26n_training"),
    ("YOLOv8n", TRAINING_DIR / "train_yolov8n.py", "yolov8n_training"),
]

def main():
    print("=" * 70)
    print("STARTING SEQUENTIAL TRAINING FOR ALL MODELS")
    print("Hyperparameters: imgsz=640, epochs=50, batch=16, lr0=0.001, optimizer=AdamW, momentum=0.9, weight_decay=0.0005")
    print("=" * 70)

    for model_name, script_path, run_name in models:
        print(f"\n>>> Starting training for {model_name}...")
        cmd = [
            sys.executable,
            str(script_path),
            "--epochs", "50",
            "--imgsz", "640",
            "--batch", "16",
            "--lr0", "0.001",
            "--optimizer", "AdamW",
            "--momentum", "0.9",
            "--weight-decay", "0.0005",
            "--device", "0",
            "--name", run_name
        ]
        
        result = subprocess.run(cmd)
        if result.returncode != 0:
            print(f"Error occurred during training {model_name} (return code: {result.returncode})")
            sys.exit(result.returncode)
        print(f">>> Completed training for {model_name}.\n")

    print("=" * 70)
    print("ALL MODEL TRAININGS COMPLETED SUCCESSFULLY!")
    print("=" * 70)

if __name__ == "__main__":
    main()
