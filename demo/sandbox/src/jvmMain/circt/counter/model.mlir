//// HW Model

//// Verilog model
// module counter (
//     input clk,
//     input reset,
//     output reg [7:0] count
// );
//     always @(posedge clk or posedge reset) begin
//         if (reset)
//             count <= 8'b0;
//         else
//             count <= count + 1;
//     end
// endmodule

//// Counter module with reset
hw.module @Dut(
  in %clk: !seq.clock,
  in %reset: i1,
  out o: i8
) {
  %zero = hw.constant 0 : i8
  %one = hw.constant 1 : i8

  %next_value = comb.add %reg, %one : i8

  %reg = seq.compreg %next_value, %clk reset %reset, %zero : i8

  hw.output %reg : i8
}

//// Basic counter
// hw.module @Dut(in %clk: !seq.clock, out o: i8) {
//   %reg = seq.compreg %in, %clk : i8
//   %one = hw.constant 1 : i8
//   %in = comb.add %reg, %one : i8
//   hw.output %reg : i8
// }

//// Testbench Module
//
// !dut_t = !arc.sim.instance<@Dut>
//
// func.func @entry() {
//   %high = seq.const_clock high
//   %low = seq.const_clock low
//
//   arc.sim.instantiate @Dut as %model {
//     // clk <= 1
//     arc.sim.set_input %model, "clk" = %high : !seq.clock, !dut_t
//     arc.sim.step %model : !dut_t
//
//     // clk <= 0
//     arc.sim.set_input %model, "clk" = %low : !seq.clock, !dut_t
//     arc.sim.step %model : !dut_t
//
//     // after 1 clock cycle
//     %out1 = arc.sim.get_port %model, "o" : i8, !dut_t
//     // printf("dut o = %d", o)
//     arc.sim.emit "Dut o", %out1 : i8
//   }
//
//   return
// }
