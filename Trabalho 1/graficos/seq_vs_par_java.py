import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

df = pd.read_csv('merge_sort_benchmark_java.csv')

array_size = df['ArraySize']
seq_avg = df['SeqAvg_ms']
seq_std = df['SeqStd_ms']
par_avg = df['ParAvg_ms']
par_std = df['ParStd_ms']

plt.figure(figsize=(12, 6))
plt.plot(array_size, seq_avg, label='Sequencial', color='#FF5733', linewidth=2.5, marker='o')
plt.fill_between(array_size, seq_avg - seq_std, seq_avg + seq_std, color='#FF5733', alpha=0.2)
plt.plot(array_size, par_avg, label='Paralelo', color='#335BFF', linewidth=2.5, marker='o')
plt.fill_between(array_size, par_avg - par_std, par_avg + par_std, color='#335BFF', alpha=0.2)

plt.xlabel('Tamanho do Array', fontsize=10, fontweight='bold')
plt.ylabel('Tempo (ms)', fontsize=10, fontweight='bold')
plt.legend(fontsize=12)
plt.grid(alpha=0.3)

# Ticks: 10 divisíveis por 1000, último >= máximo do array
min_tick = int(np.floor(array_size.min() / 1000) * 1000)
max_tick = int(np.ceil(array_size.max() / 1000) * 1000)
step = (max_tick - min_tick) // 9
step = int(np.ceil(step / 1000) * 1000)
ticks = [min_tick + i * step for i in range(9)]
ticks.append(max_tick)  # último tick exato e divisível por 1000

plt.xticks(ticks)

plt.savefig('merge_sort_benchmark_java.png', dpi=600, bbox_inches='tight')
plt.show()