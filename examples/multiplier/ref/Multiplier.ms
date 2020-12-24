typedef struct {
    Bit#(n) a;
    Bit#(n) b; 
} MultiplierInput#( Integer n ); 

module FastFoldedMultiplier#( Integer n );

    input Maybe#(MultiplierInput#(n))
        in default = Invalid;

    RegU#(Bit#(n)) a;
    RegU#(Bit#(n)) b;
    RegU#(Bit#(n)) prod;
    RegU#(Bit#(n)) tp;
    Reg#(Maybe#(Bit#(log2(n)+1))) i(Invalid);

    rule mulStep;
        if (isValid(in)) begin
            let in_val = fromMaybe(?, in);
            a <= in_val.a;
            b <= in_val.b;
            prod <= 0;
            tp <= 0;
            i <= Valid(0);
        end else if (isValid(i) && fromMaybe(?,i) < n) begin 
            Bit#(n) m = (b[0] == 0) ? 0 : a;
            b <= b >> 1;
            Bit#(n+1) sum = zeroExtend(m) + zeroExtend(tp);
            prod <= {sum[0], prod[n-1:1]};
            tp <= sum[n:1];
            i <= Valid(fromMaybe(?, i) + 1);
        end
    endrule

    method Maybe#(Bit#(2*n)) getResult;
        Bool resultValid = isValid(i) && (fromMaybe(?, i)==n); 
        Maybe#(Bit#(n+n)) res = (resultValid) ? Valid({ tp, prod }) : Invalid;
        return res;
    endmethod
endmodule
