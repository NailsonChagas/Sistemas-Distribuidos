import pandas as pd
import numpy as np

# =========================
# LEITURA DOS CSVs
# =========================
df_c = pd.read_csv('C_merge_sort_benchmark.csv')
df_java = pd.read_csv('java_merge_sort_benchmark.csv')

report_lines = []

# =========================
# CABEÇALHO
# =========================
report_lines.append("RELATÓRIO DE DESEMPENHO - MERGE SORT (C vs JAVA)\n")
report_lines.append("="*60 + "\n\n")

# =========================
# 1. RESUMO GERAL
# =========================
report_lines.append("1. RESUMO GERAL\n")

report_lines.append(f"- Tamanhos testados (C): {df_c['ArraySize'].min()} até {df_c['ArraySize'].max()}\n")
report_lines.append(f"- Tamanhos testados (Java): {df_java['ArraySize'].min()} até {df_java['ArraySize'].max()}\n\n")

# =========================
# 2. MÉDIAS DE TEMPO
# =========================
report_lines.append("2. MÉDIAS DE TEMPO (ms)\n")

report_lines.append(f"C Sequencial: {df_c['SeqAvg_ms'].mean():.3f}\n")
report_lines.append(f"C Pthread:    {df_c['PT_Avg_ms'].mean():.3f}\n")
report_lines.append(f"C OpenMP:     {df_c['OMP_Avg_ms'].mean():.3f}\n")

report_lines.append(f"Java Sequencial: {df_java['SeqAvg_ms'].mean():.3f}\n")
report_lines.append(f"Java Paralelo:   {df_java['ParAvg_ms'].mean():.3f}\n\n")

# =========================
# 3. SPEEDUP MÉDIO
# =========================
report_lines.append("3. SPEEDUP MÉDIO\n")

report_lines.append(f"C Pthread: {df_c['PT_SpeedupAvg'].mean():.3f}\n")
report_lines.append(f"C OpenMP:  {df_c['OMP_SpeedupAvg'].mean():.3f}\n")
report_lines.append(f"Java:      {df_java['SpeedupAvg'].mean():.3f}\n\n")

# =========================
# 4. MELHOR IMPLEMENTAÇÃO POR TAMANHO
# =========================
report_lines.append("4. MELHOR IMPLEMENTAÇÃO POR TAMANHO\n")

wins = {
    "C Seq": 0,
    "C Pthread": 0,
    "C OpenMP": 0,
    "Java Seq": 0,
    "Java Par": 0
}

threshold = None

for i in range(len(df_c)):
    size = df_c['ArraySize'][i]

    tempos = {
        "C Seq": df_c['SeqAvg_ms'][i],
        "C Pthread": df_c['PT_Avg_ms'][i],
        "C OpenMP": df_c['OMP_Avg_ms'][i]
    }

    if i < len(df_java):
        tempos["Java Seq"] = df_java['SeqAvg_ms'][i]
        tempos["Java Par"] = df_java['ParAvg_ms'][i]

    melhor = min(tempos, key=tempos.get)
    wins[melhor] += 1

    report_lines.append(f"Array {size}: Melhor = {melhor} ({tempos[melhor]:.3f} ms)\n")

    # detectar ponto onde OpenMP vence o sequencial
    if threshold is None:
        if df_c['OMP_Avg_ms'][i] < df_c['SeqAvg_ms'][i]:
            threshold = size

report_lines.append("\n")

# =========================
# 5. PONTO DE VIRADA
# =========================
report_lines.append("5. PONTO DE VIRADA DO PARALELISMO\n")

if threshold:
    report_lines.append(f"- OpenMP passa a ser mais rápido que o sequencial a partir de ~{threshold} elementos.\n\n")
else:
    report_lines.append("- Paralelismo não superou o sequencial.\n\n")

# =========================
# 6. DOMINÂNCIA
# =========================
report_lines.append("6. DOMINÂNCIA POR IMPLEMENTAÇÃO\n")

for k, v in wins.items():
    report_lines.append(f"- {k}: {v} vitórias\n")

best_overall = max(wins, key=wins.get)
report_lines.append(f"\n- Implementação dominante: {best_overall}\n\n")

# =========================
# 7. ANÁLISE AVANÇADA
# =========================
report_lines.append("7. ANÁLISE AVANÇADA\n")

# comparação OpenMP vs pthread
if df_c['OMP_SpeedupAvg'].mean() > df_c['PT_SpeedupAvg'].mean():
    report_lines.append("- OpenMP apresenta melhor desempenho médio que Pthreads.\n")
else:
    report_lines.append("- Pthreads apresenta desempenho competitivo com OpenMP.\n")

# escalabilidade
growth_seq = df_c['SeqAvg_ms'].iloc[-1] / df_c['SeqAvg_ms'].iloc[0]
growth_omp = df_c['OMP_Avg_ms'].iloc[-1] / df_c['OMP_Avg_ms'].iloc[0]

if growth_omp < growth_seq:
    report_lines.append("- OpenMP apresenta melhor escalabilidade que o sequencial.\n")
else:
    report_lines.append("- Escalabilidade semelhante entre sequencial e paralelo.\n")

# C vs Java
if df_c['OMP_Avg_ms'].mean() < df_java['ParAvg_ms'].mean():
    report_lines.append("- C paralelo (OpenMP) supera Java paralelo.\n")
else:
    report_lines.append("- Java paralelo apresenta desempenho competitivo com C.\n")

# ganho real
if df_java['SpeedupAvg'].mean() > 1:
    report_lines.append("- Java apresenta ganho real com paralelismo.\n")
else:
    report_lines.append("- Paralelismo em Java não trouxe ganho consistente.\n")

report_lines.append("\n")

# =========================
# 8. ANOMALIAS
# =========================
report_lines.append("8. ANOMALIAS DETECTADAS\n")

found_anomaly = False

for i in range(1, len(df_c)):
    curr = df_c['OMP_Avg_ms'][i]
    prev = df_c['OMP_Avg_ms'][i - 1]

    if curr > prev * 1.5:
        size = df_c['ArraySize'][i]
        report_lines.append(f"- Possível anomalia em {size} (salto de desempenho)\n")
        found_anomaly = True

if not found_anomaly:
    report_lines.append("- Nenhuma anomalia significativa detectada.\n")

report_lines.append("\n")

# =========================
# SALVAR RELATÓRIO
# =========================
with open("relatorio_merge_sort.txt", "w") as f:
    f.writelines(report_lines)

print("Relatório gerado com sucesso: relatorio_merge_sort.txt")