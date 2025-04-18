func.func private @run_alu(%op : i5, %lhs : i32, %rhs : i32) {
  arc.sim.instantiate @alu as %model {
    arc.sim.set_input %model, "lhs" = %lhs : i32, !arc.sim.instance<@alu>
    arc.sim.set_input %model, "rhs" = %rhs : i32, !arc.sim.instance<@alu>
    arc.sim.set_input %model, "alu_op" = %op : i5, !arc.sim.instance<@alu>

    arc.sim.step %model : !arc.sim.instance<@alu>

    %out = arc.sim.get_port %model, "out" : i32, !arc.sim.instance<@alu>
    %out_bit0 = arc.sim.get_port %model, "out_bit0" : i1, !arc.sim.instance<@alu>

    arc.sim.emit "out", %out : i32
    arc.sim.emit "out_bit0", %out_bit0 : i1
  }

  return
}

!run_t = (i5, i32, i32) -> ()

func.func @entry() {
  %c-0x12345677 = hw.constant -0x12345677 : i32
  %c-1234 = hw.constant -1234 : i32
  %c-123 = hw.constant -123 : i32
  %c-3 = hw.constant -3 : i32
  %c0 = hw.constant 0 : i32
  %c1 = hw.constant 1 : i32
  %c3 = hw.constant 3 : i32
  %c7 = hw.constant 7 : i32
  %c42 = hw.constant 42 : i32
  %c0x12345678 = hw.constant 0x12345678 : i32
  %c0x7fffffff = hw.constant 0x7fffffff : i32
  %c0x80000001 = hw.constant 0x80000001 : i32
  %c0x87654321 = hw.constant 0x87654321 : i32
  %c0xffffffff = hw.constant 0xffffffff : i32

  // 0b01100110100110010011110011110000
  %c0x66993cf0 = hw.constant 0x66993cf0 : i32
  // 0b11110011000011001001011010010110
  %c0xf30c9696 = hw.constant 0xf30c9696 : i32

  %alu_op_zero = hw.constant 0 : i5
  %alu_op_add = hw.constant 1 : i5
  %alu_op_sub = hw.constant 2 : i5
  %alu_op_slt = hw.constant 3 : i5
  %alu_op_sltu = hw.constant 4 : i5
  %alu_op_xor = hw.constant 5 : i5
  %alu_op_or = hw.constant 6 : i5
  %alu_op_and = hw.constant 7 : i5
  %alu_op_sll = hw.constant 8 : i5
  %alu_op_srl = hw.constant 9 : i5
  %alu_op_sra = hw.constant 10 : i5
  %alu_op_seq = hw.constant 11 : i5

  // ALU_OP_ZERO (zero out) ////////////////////////////////////////////////////
  // CHECK: out = 0
  // CHECK: out_bit0 = 0
  func.call @run_alu(%alu_op_zero, %c42, %c-1234) : !run_t

  // ALU_OP_ADD (addition) /////////////////////////////////////////////////////
  // CHECK: out = fffffb58
  // CHECK: out_bit0 = 0
  func.call @run_alu(%alu_op_add, %c42, %c-1234) : !run_t

  // CHECK: out = 1
  // CHECK: out_bit0 = 1
  func.call @run_alu(%alu_op_add, %c-0x12345677, %c0x12345678) : !run_t

  // CHECK: out = 2
  // CHECK: out_bit0 = 0
  func.call @run_alu(%alu_op_add, %c0x80000001, %c0x80000001) : !run_t

  // ALU_OP_SUB (subtraction) //////////////////////////////////////////////////
  // CHECK: out = 4cf
  // CHECK: out_bit0 = 1
  func.call @run_alu(%alu_op_sub, %c-3, %c-1234) : !run_t

  // CHECK: out = ffffffd6
  // CHECK: out_bit0 = 0
  func.call @run_alu(%alu_op_sub, %c0, %c42) : !run_t

  // ALU_OP_SLT (set less-than, signed) ////////////////////////////////////////
  // CHECK: out = 0
  func.call @run_alu(%alu_op_slt, %c0, %c-3) : !run_t

  // CHECK: out = 1
  func.call @run_alu(%alu_op_slt, %c0x80000001, %c0x7fffffff) : !run_t

  // CHECK: out = 1
  func.call @run_alu(%alu_op_slt, %c-3, %c42) : !run_t

  // CHECK: out = 1
  func.call @run_alu(%alu_op_slt, %c42, %c0x12345678) : !run_t

  // CHECK: out = 0
  func.call @run_alu(%alu_op_slt, %c0x12345678, %c42) : !run_t

  // CHECK: out = 0
  func.call @run_alu(%alu_op_slt, %c42, %c42) : !run_t

  // ALU_OP_SLTU (set less-than, unsigned) /////////////////////////////////////
  // CHECK: out = 1
  func.call @run_alu(%alu_op_sltu, %c0, %c-3) : !run_t

  // CHECK: out = 0
  func.call @run_alu(%alu_op_sltu, %c0x80000001, %c0x7fffffff) : !run_t

  // CHECK: out = 0
  func.call @run_alu(%alu_op_sltu, %c-3, %c42) : !run_t

  // CHECK: out = 1
  func.call @run_alu(%alu_op_sltu, %c42, %c0x12345678) : !run_t

  // CHECK: out = 0
  func.call @run_alu(%alu_op_sltu, %c0x12345678, %c42) : !run_t

  // CHECK: out = 0
  func.call @run_alu(%alu_op_sltu, %c42, %c42) : !run_t

  // ALU_OP_XOR (bitwise exclusive or) /////////////////////////////////////////
  // CHECK: out = 9595aa66
  func.call @run_alu(%alu_op_xor, %c0x66993cf0, %c0xf30c9696) : !run_t

  // CHECK: out = 0
  func.call @run_alu(%alu_op_xor, %c0x66993cf0, %c0x66993cf0) : !run_t

  // CHECK: out = 66993cf0
  func.call @run_alu(%alu_op_xor, %c0x66993cf0, %c0) : !run_t

  // CHECK: out = 9966c30f
  func.call @run_alu(%alu_op_xor, %c0x66993cf0, %c0xffffffff) : !run_t

  // ALU_OP_OR (bitwise or) ////////////////////////////////////////////////////
  // CHECK: out = f79dbef6
  func.call @run_alu(%alu_op_or, %c0x66993cf0, %c0xf30c9696) : !run_t

  // CHECK: out = 66993cf0
  func.call @run_alu(%alu_op_or, %c0x66993cf0, %c0x66993cf0) : !run_t

  // CHECK: out = 66993cf0
  func.call @run_alu(%alu_op_or, %c0x66993cf0, %c0) : !run_t

  // CHECK: out = ffffffff
  func.call @run_alu(%alu_op_or, %c0x66993cf0, %c0xffffffff) : !run_t

  // ALU_OP_AND (bitwise and) //////////////////////////////////////////////////
  // CHECK: out = 62081490
  func.call @run_alu(%alu_op_and, %c0x66993cf0, %c0xf30c9696) : !run_t

  // CHECK: out = 66993cf0
  func.call @run_alu(%alu_op_and, %c0x66993cf0, %c0x66993cf0) : !run_t

  // CHECK: out = 0
  func.call @run_alu(%alu_op_and, %c0x66993cf0, %c0) : !run_t

  // CHECK: out = 66993cf0
  func.call @run_alu(%alu_op_and, %c0x66993cf0, %c0xffffffff) : !run_t

  // ALU_OP_SLL (shift left, logical) //////////////////////////////////////////
  // CHECK: out = 2
  func.call @run_alu(%alu_op_sll, %c1, %c1) : !run_t

  // CHECK: out = 2
  func.call @run_alu(%alu_op_sll, %c0x80000001, %c1) : !run_t

  // CHECK: out = 200
  func.call @run_alu(%alu_op_sll, %c1, %c-0x12345677) : !run_t

  // CHECK: out = ffffffff
  func.call @run_alu(%alu_op_sll, %c0xffffffff, %c0) : !run_t

  // ALU_OP_SRL (shift right, logical) /////////////////////////////////////////
  // CHECK: out = 7fffffff
  func.call @run_alu(%alu_op_srl, %c0xffffffff, %c1) : !run_t

  // CHECK: out = 334c9e
  func.call @run_alu(%alu_op_srl, %c0x66993cf0, %c-0x12345677) : !run_t

  // CHECK: out = 12345678
  func.call @run_alu(%alu_op_srl, %c0x12345678, %c0) : !run_t

  // CHECK: out = 0
  func.call @run_alu(%alu_op_srl, %c7, %c3) : !run_t

  // ALU_OP_SRA (shift right, arithmetic) //////////////////////////////////////
  // CHECK: out = f0000000
  func.call @run_alu(%alu_op_sra, %c0x80000001, %c3) : !run_t

  // CHECK: out = ffc3b2a1
  func.call @run_alu(%alu_op_sra, %c0x87654321, %c-0x12345677) : !run_t

  // CHECK: out = 12345678
  func.call @run_alu(%alu_op_sra, %c0x12345678, %c0) : !run_t

  // CHECK: out = 0
  func.call @run_alu(%alu_op_sra, %c7, %c3) : !run_t

  // ALU_OP_SEQ (set equals) ///////////////////////////////////////////////////
  // CHECK: out = 1
  func.call @run_alu(%alu_op_seq, %c0x12345678, %c0x12345678) : !run_t

  // CHECK: out = 0
  func.call @run_alu(%alu_op_seq, %c0, %c1) : !run_t

  return
}
hw.module @alu(in %lhs : i32, in %rhs : i32, in %alu_op : i5, out out : i32, out out_bit0 : i1) {
  %c0_i31 = hw.constant 0 : i31
  %true = hw.constant true
  %c0_i27 = hw.constant 0 : i27
  %0 = llhd.constant_time <0ns, 0d, 1e>
  %c0_i32 = hw.constant 0 : i32
  %c11_i5 = hw.constant 11 : i5
  %c10_i5 = hw.constant 10 : i5
  %c9_i5 = hw.constant 9 : i5
  %c8_i5 = hw.constant 8 : i5
  %c7_i5 = hw.constant 7 : i5
  %c6_i5 = hw.constant 6 : i5
  %c5_i5 = hw.constant 5 : i5
  %c4_i5 = hw.constant 4 : i5
  %c3_i5 = hw.constant 3 : i5
  %c2_i5 = hw.constant 2 : i5
  %c1_i5 = hw.constant 1 : i5
  %c0_i5 = hw.constant 0 : i5
  %2 = comb.extract %84 from 0 : (i32) -> i1
  %3 = comb.icmp ceq %alu_op, %c0_i5 : i5
  %4 = comb.icmp ceq %alu_op, %c1_i5 : i5
  %5 = comb.icmp ceq %alu_op, %c2_i5 : i5
  %6 = comb.add %lhs, %rhs : i32
  %7 = comb.icmp ceq %alu_op, %c3_i5 : i5
  %8 = comb.sub %lhs, %rhs : i32
  %9 = comb.icmp ceq %alu_op, %c4_i5 : i5
  %10 = comb.icmp slt %lhs, %rhs : i32
  %11 = comb.concat %c0_i31, %10 : i31, i1
  %12 = comb.icmp ceq %alu_op, %c5_i5 : i5
  %13 = comb.icmp ult %lhs, %rhs : i32
  %14 = comb.concat %c0_i31, %13 : i31, i1
  %15 = comb.icmp ceq %alu_op, %c6_i5 : i5
  %16 = comb.xor %lhs, %rhs : i32
  %17 = comb.icmp ceq %alu_op, %c7_i5 : i5
  %18 = comb.or %lhs, %rhs : i32
  %19 = comb.icmp ceq %alu_op, %c8_i5 : i5
  %20 = comb.and %lhs, %rhs : i32
  %21 = comb.icmp ceq %alu_op, %c9_i5 : i5
  %22 = comb.extract %rhs from 0 : (i32) -> i5
  %23 = comb.extract %rhs from 4 : (i32) -> i1
  %24 = comb.replicate %23 : (i1) -> i27
  %25 = comb.concat %24, %22 : i27, i5
  %26 = comb.shl %lhs, %25 : i32
  %27 = comb.icmp ceq %alu_op, %c10_i5 : i5
  %28 = comb.extract %rhs from 0 : (i32) -> i5
  %29 = comb.extract %rhs from 4 : (i32) -> i1
  %30 = comb.replicate %29 : (i1) -> i27
  %31 = comb.concat %30, %28 : i27, i5
  %32 = comb.shru %lhs, %31 : i32
  %33 = comb.icmp ceq %alu_op, %c11_i5 : i5
  %34 = comb.extract %rhs from 0 : (i32) -> i5
  %35 = comb.concat %c0_i27, %34 : i27, i5
  %36 = comb.shrs %lhs, %35 : i32
  %37 = comb.icmp eq %lhs, %rhs : i32
  %38 = comb.concat %c0_i31, %37 : i31, i1
  %39 = comb.xor %3, %true : i1
  %40 = comb.and %39, %4 : i1
  %41 = comb.mux %40, %6, %c0_i32 : i32
  %42 = comb.xor %4, %true : i1
  %43 = comb.and %39, %42 : i1
  %44 = comb.and %43, %5 : i1
  %45 = comb.mux %44, %8, %41 : i32
  %46 = comb.xor %5, %true : i1
  %47 = comb.and %43, %46 : i1
  %48 = comb.and %47, %7 : i1
  %49 = comb.mux %48, %11, %45 : i32
  %50 = comb.xor %7, %true : i1
  %51 = comb.and %47, %50 : i1
  %52 = comb.and %51, %9 : i1
  %53 = comb.mux %52, %14, %49 : i32
  %54 = comb.xor %9, %true : i1
  %55 = comb.and %51, %54 : i1
  %56 = comb.and %55, %12 : i1
  %57 = comb.mux %56, %16, %53 : i32
  %58 = comb.xor %12, %true : i1
  %59 = comb.and %55, %58 : i1
  %60 = comb.and %59, %15 : i1
  %61 = comb.mux %60, %18, %57 : i32
  %62 = comb.xor %15, %true : i1
  %63 = comb.and %59, %62 : i1
  %64 = comb.and %63, %17 : i1
  %65 = comb.mux %64, %20, %61 : i32
  %66 = comb.xor %17, %true : i1
  %67 = comb.and %63, %66 : i1
  %68 = comb.and %67, %19 : i1
  %69 = comb.mux %68, %26, %65 : i32
  %70 = comb.xor %19, %true : i1
  %71 = comb.and %67, %70 : i1
  %72 = comb.and %71, %21 : i1
  %73 = comb.mux %72, %32, %69 : i32
  %74 = comb.xor %21, %true : i1
  %75 = comb.and %71, %74 : i1
  %76 = comb.and %75, %27 : i1
  %77 = comb.mux %76, %36, %73 : i32
  %78 = comb.xor %27, %true : i1
  %79 = comb.and %75, %78 : i1
  %80 = comb.and %79, %33 : i1
  %81 = comb.mux %80, %38, %77 : i32
  %82 = comb.xor %33, %true : i1
  %83 = comb.and %79, %82 : i1
  %84 = comb.mux %83, %c0_i32, %81 : i32
  hw.output %84, %2 : i32, i1
}

