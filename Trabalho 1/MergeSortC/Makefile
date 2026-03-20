# Makefile para Merge Sort (sequencial e paralelo)

# Compilador e flags
CC = gcc
CFLAGS = -Wall -Wextra -O3 -pthread -I./inc

# Diretórios
SRC_DIR = src
INC_DIR = inc
OBJ_DIR = build

# Arquivos fonte
SRCS = $(SRC_DIR)/merge_sort_parallel.c \
       $(SRC_DIR)/merge_sort_sequential.c \
       main.c

# Objetos correspondentes
OBJS = $(patsubst %.c,$(OBJ_DIR)/%.o,$(SRCS))

# Executável
TARGET = merge_sort

# Regra padrão
all: $(TARGET)

# Linkagem do executável
$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) -o $@ $^ -lm

# Compilação dos objetos
$(OBJ_DIR)/%.o: %.c
	@mkdir -p $(dir $@)
	$(CC) $(CFLAGS) -c $< -o $@

# Limpeza
clean:
	rm -rf $(OBJ_DIR) $(TARGET)

.PHONY: all clean