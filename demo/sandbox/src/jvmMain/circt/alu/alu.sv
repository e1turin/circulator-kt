`timescale 1ns / 1ps

import enums::*;

module alu (
    input logic signed [31:0] lhs,
    input logic signed [31:0] rhs,
    input alu_op_t alu_op,
    output logic [31:0] out,
    output logic out_bit0
);

    assign out_bit0 = out[0];

    typedef logic unsigned [31:0] uword_t;

    always_comb begin
        case (alu_op)
            ALU_OP_ZERO: out = 32'b0;
            ALU_OP_ADD:  out = lhs + rhs;
            ALU_OP_SUB:  out = lhs - rhs;
            ALU_OP_SLT:  out = (lhs < rhs) ? 32'd1 : 32'b0;
            ALU_OP_SLTU: out = (uword_t'(lhs) < uword_t'(rhs)) ? 32'd1 : 32'b0;
            ALU_OP_XOR:  out = lhs ^ rhs;
            ALU_OP_OR:   out = lhs | rhs;
            ALU_OP_AND:  out = lhs & rhs;
            ALU_OP_SLL:  out = uword_t'(lhs) << uword_t'(rhs[4:0]);
            ALU_OP_SRL:  out = uword_t'(lhs) >> uword_t'(rhs[4:0]);
            ALU_OP_SRA:  out = lhs >>> rhs[4:0];
            ALU_OP_SEQ:  out = (lhs == rhs) ? 32'd1 : 32'b0;

            default: out = 32'b0;
        endcase
    end

endmodule
