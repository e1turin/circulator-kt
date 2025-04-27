`timescale 1ns / 1ps

package enums;

    typedef enum logic {
        PROC_STATE_INSTR_FETCH,
        PROC_STATE_INSTR_DECODING
    } proc_state_t;

    typedef enum logic [4:0] {
        MCAUSE_MSI = {1'b1, 4'd3},
        MCAUSE_MTI = {1'b1, 4'd7},
        MCAUSE_MEI = {1'b1, 4'd11},
        MCAUSE_INSTR_ADDR_MISALIGN = {1'b0, 4'd0},
        MCAUSE_INSTR_ADDR_FAULT = {1'b0, 4'd1},
        MCAUSE_ILLEGAL_INSTR = {1'b0, 4'd2},
        MCAUSE_BREAKPOINT = {1'b0, 4'd3},
        MCAUSE_LOAD_ADDR_MISALIGN = {1'b0, 4'd4},
        MCAUSE_LOAD_ACCESS_FAULT = {1'b0, 4'd5},
        MCAUSE_STORE_ADDR_MISALIGN = {1'b0, 4'd6},
        MCAUSE_STORE_ACCESS_FAULT = {1'b0, 4'd7},
        MCAUSE_M_ECALL = {1'b0, 4'd11}
    } mcause_t;

    typedef enum logic [4:0] {
        CSR_NONE,
        CSR_MISA,
        CSR_MVENDORID,
        CSR_MARCHID,
        CSR_MIMPID,
        CSR_MHARTID,
        CSR_MSTATUS,
        CSR_MTVEC,
        CSR_MIP,
        CSR_MIE,
        CSR_MCYCLE,
        CSR_MCYCLEH,
        CSR_MINSTRET,
        CSR_MINSTRETH,
        CSR_MCOUNTEREN,
        CSR_MCOUNTINHIBIT,
        CSR_MSCRATCH,
        CSR_MEPC,
        CSR_MCAUSE,
        CSR_MTVAL
    } csr_t;

    typedef enum logic [2:0] {
        INSTR_TYPE_R = 3'b000,
        INSTR_TYPE_I = 3'b001,
        INSTR_TYPE_S = 3'b010,
        INSTR_TYPE_B = 3'b011,
        INSTR_TYPE_U = 3'b100,
        INSTR_TYPE_J = 3'b101
    } instr_type_t;

    typedef enum logic [4:0] {
        ALU_OP_ZERO = 5'b00000,
        ALU_OP_ADD  = 5'b00001,
        ALU_OP_SUB  = 5'b00010,
        ALU_OP_SLT  = 5'b00011,
        ALU_OP_SLTU = 5'b00100,
        ALU_OP_XOR  = 5'b00101,
        ALU_OP_OR   = 5'b00110,
        ALU_OP_AND  = 5'b00111,
        ALU_OP_SLL  = 5'b01000,
        ALU_OP_SRL  = 5'b01001,
        ALU_OP_SRA  = 5'b01010,
        ALU_OP_SEQ  = 5'b01011
    } alu_op_t;

endpackage
