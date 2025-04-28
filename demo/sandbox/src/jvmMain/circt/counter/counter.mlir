//// Counter module with reset
hw.module @Counter(
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

//// Verilog model
// module Counter (
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

