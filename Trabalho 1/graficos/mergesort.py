import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

# =========================
# LEITURA DOS CSVs
# =========================
df_c = pd.read_csv('C_merge_sort_benchmark.csv')
df_java = pd.read_csv('java_merge_sort_benchmark.csv')

# =========================
# DADOS C
# =========================
array_size_c = df_c['ArraySize']

seq_avg_c = df_c['SeqAvg_ms']
seq_std_c = df_c['SeqStd_ms']

pt_avg = df_c['PT_Avg_ms']
pt_std = df_c['PT_Std_ms']

omp_avg = df_c['OMP_Avg_ms']
omp_std = df_c['OMP_Std_ms']

pt_speedup = df_c['PT_SpeedupAvg']
pt_speedup_std = df_c['PT_SpeedupStd']

omp_speedup = df_c['OMP_SpeedupAvg']
omp_speedup_std = df_c['OMP_SpeedupStd']

# =========================
# DADOS JAVA
# =========================
array_size_j = df_java['ArraySize']

seq_avg_j = df_java['SeqAvg_ms']
seq_std_j = df_java['SeqStd_ms']

par_avg_j = df_java['ParAvg_ms']
par_std_j = df_java['ParStd_ms']

speedup_j = df_java['SpeedupAvg']
speedup_std_j = df_java['SpeedupStd']

# =========================
# GRÁFICO 1 - TEMPO
# =========================
plt.figure(figsize=(12, 6))

# --- C ---
plt.plot(array_size_c, seq_avg_c, label='C - Sequencial', linewidth=1.5, marker='o', markersize=3)
plt.fill_between(array_size_c, seq_avg_c - seq_std_c, seq_avg_c + seq_std_c, alpha=0.2)

plt.plot(array_size_c, pt_avg, label='C - Pthread', linewidth=1.5, marker='o', markersize=3)
plt.fill_between(array_size_c, pt_avg - pt_std, pt_avg + pt_std, alpha=0.2)

plt.plot(array_size_c, omp_avg, label='C - OpenMP', linewidth=1.5, marker='o', markersize=3)
plt.fill_between(array_size_c, omp_avg - omp_std, omp_avg + omp_std, alpha=0.2)

# --- JAVA ---
plt.plot(array_size_j, seq_avg_j, label='Java - Sequencial', linewidth=1.5, marker='s', markersize=3)
plt.fill_between(array_size_j, seq_avg_j - seq_std_j, seq_avg_j + seq_std_j, alpha=0.2)

plt.plot(array_size_j, par_avg_j, label='Java - Thread', linewidth=1.5, marker='s', markersize=3)
plt.fill_between(array_size_j, par_avg_j - par_std_j, par_avg_j + par_std_j, alpha=0.2)

plt.xlabel('Tamanho do Array', fontsize=10, fontweight='bold')
plt.ylabel('Tempo (ms)', fontsize=10, fontweight='bold')
plt.legend(fontsize=10)
plt.grid(alpha=0.3)

# Ticks
all_sizes = pd.concat([array_size_c, array_size_j])
min_tick = int(np.floor(all_sizes.min() / 1000) * 1000)
max_tick = int(np.ceil(all_sizes.max() / 1000) * 1000)

step = (max_tick - min_tick) // 9
step = int(np.ceil(step / 1000) * 1000)

ticks = [min_tick + i * step for i in range(9)]
ticks.append(max_tick)

plt.xticks(ticks)

plt.savefig('merge_sort_tempo_c_vs_java.png', dpi=600, bbox_inches='tight')


# =========================
# GRÁFICO 2 - SPEEDUP
# =========================
plt.figure(figsize=(12, 6))

# --- C ---
plt.plot(array_size_c, pt_speedup, label='C - Pthread', linewidth=2.5, marker='o')
plt.fill_between(array_size_c, pt_speedup - pt_speedup_std, pt_speedup + pt_speedup_std, alpha=0.2)

plt.plot(array_size_c, omp_speedup, label='C - OpenMP', linewidth=2.5, marker='o')
plt.fill_between(array_size_c, omp_speedup - omp_speedup_std, omp_speedup + omp_speedup_std, alpha=0.2)

# --- JAVA ---
plt.plot(array_size_j, speedup_j, label='Java - Thread', linewidth=2.5, marker='s')
plt.fill_between(array_size_j, speedup_j - speedup_std_j, speedup_j + speedup_std_j, alpha=0.2)

plt.xlabel('Tamanho do Array', fontsize=10, fontweight='bold')
plt.ylabel('Speedup', fontsize=10, fontweight='bold')
plt.legend(fontsize=10)
plt.grid(alpha=0.3)

plt.xticks(ticks)
plt.savefig('merge_sort_speedup_c_vs_java.png', dpi=600, bbox_inches='tight')
