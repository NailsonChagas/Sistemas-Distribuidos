import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

df = pd.read_csv('C_dot_product_benchmark.csv')

array_size = df['ArraySize']

seq_avg = df['SeqAvg_ms']
seq_std = df['SeqStd_ms']

omp_avg = df['OMP_Avg_ms']
omp_std = df['OMP_Std_ms']

omp_speedup = df['OMP_SpeedupAvg']
omp_speedup_std = df['OMP_SpeedupStd']

plt.figure(figsize=(12, 6))

plt.plot(array_size, seq_avg, label='Sequencial', linewidth=1.8, marker='o', markersize=3)
plt.fill_between(array_size, seq_avg - seq_std, seq_avg + seq_std, alpha=0.2)

plt.plot(array_size, omp_avg, label='OpenMP', linewidth=1.8, marker='o', markersize=3)
plt.fill_between(array_size, omp_avg - omp_std, omp_avg + omp_std, alpha=0.2)

plt.xlabel('Tamanho do Vetor', fontsize=10, fontweight='bold')
plt.ylabel('Tempo (ms)', fontsize=10, fontweight='bold')
plt.title('Benchmark - Produto Escalar', fontsize=12, fontweight='bold')

plt.legend(fontsize=10)
plt.grid(alpha=0.3)

min_tick = int(np.floor(array_size.min() / 10000) * 10000)
max_tick = int(np.ceil(array_size.max() / 10000) * 10000)

step = (max_tick - min_tick) // 8
step = int(np.ceil(step / 10000) * 10000)

ticks = [min_tick + i * step for i in range(8)]
ticks.append(max_tick)

plt.xticks(ticks)

plt.savefig('dot_product_tempo.png', dpi=600, bbox_inches='tight')


plt.figure(figsize=(12, 6))

plt.plot(array_size, omp_speedup, label='OpenMP', linewidth=2.2, marker='o')
plt.fill_between(array_size,
                 omp_speedup - omp_speedup_std,
                 omp_speedup + omp_speedup_std,
                 alpha=0.2)

plt.xlabel('Tamanho do Vetor', fontsize=10, fontweight='bold')
plt.ylabel('Speedup', fontsize=10, fontweight='bold')
plt.title('Speedup - Produto Escalar (OpenMP)', fontsize=12, fontweight='bold')

plt.legend(fontsize=10)
plt.grid(alpha=0.3)

plt.xticks(ticks)

plt.savefig('dot_product_speedup.png', dpi=600, bbox_inches='tight')