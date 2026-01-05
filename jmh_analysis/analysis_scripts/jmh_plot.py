import json
import sys
from pathlib import Path
import matplotlib.pyplot as plt

def load_records(path: Path):
    data = json.loads(path.read_text())
    # JMH JSON is typically a list of benchmark result objects
    records = []
    for item in data:
        bench = item.get("benchmark", "unknown")
        params = item.get("params") or {}
        metric = item.get("primaryMetric") or {}
        score = metric.get("score", None)
        err = metric.get("scoreError", 0.0)
        unit = metric.get("scoreUnit", "")
        mode = item.get("mode", "")
        # label includes params to distinguish variants
        if params:
            p = ", ".join(f"{k}={v}" for k, v in sorted(params.items()))
            label = f"{bench} [{p}]"
        else:
            label = bench
        records.append((label, score, err, unit, mode))
    return records

def main(paths):
    all_records = []
    for p in paths:
        all_records.extend(load_records(Path(p)))

    # drop missing scores
    all_records = [r for r in all_records if r[1] is not None]
    if not all_records:
        print("No benchmark scores found.")
        return

    labels = [r[0] for r in all_records]
    scores = [r[1] for r in all_records]
    errs   = [r[2] for r in all_records]
    unit   = all_records[0][3]
    mode   = all_records[0][4]

    # Plot
    plt.figure()
    x = range(len(scores))
    plt.bar(x, scores, yerr=errs, capsize=3)
    plt.xticks(x, labels, rotation=45, ha="right")
    plt.ylabel(f"Score ({unit})")
    title = "JMH Results"
    if mode:
        title += f" [{mode}]"
    plt.title(title)
    plt.tight_layout()
    plt.show()

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python plot_jmh.py result1.json [result2.json ...]")
        sys.exit(1)
    main(sys.argv[1:])
