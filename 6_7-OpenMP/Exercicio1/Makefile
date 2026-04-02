CC = gcc
CFLAGS = -Wall -Wextra -O3 -fopenmp -I./inc

SRC_DIR = src
INC_DIR = inc
OBJ_DIR = build
TARGET = dot_product

SRCS = $(SRC_DIR)/aux.c \
	   $(SRC_DIR)/dot_product.c \
       main.c

OBJS = $(patsubst %.c,$(OBJ_DIR)/%.o,$(SRCS))

all: $(TARGET)

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) -o $@ $^ -lm

$(OBJ_DIR)/%.o: %.c
	@mkdir -p $(dir $@)
	$(CC) $(CFLAGS) -c $< -o $@

clean:
	rm -rf $(OBJ_DIR) $(TARGET)

.PHONY: all clean