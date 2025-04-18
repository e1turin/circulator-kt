
struct State {         // sizeof State == 8
  char clk;            // +0
  char reset;          // +1
  char clk_internal;   // +2
  char reset_internal; // +3
  char _gap;           //
  char reg_internal;   // +5
  char o_internal;     // +6
  char o;              // +7
};

extern void Dut_eval(void *state);
