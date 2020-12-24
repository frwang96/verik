bsvimport GenericRandomizer;

function Bit#(2*n) multiply_unsigned_ref#(Integer n)(Bit#(n) a, Bit#(n) b);
    return zeroExtend(a) * zeroExtend(b);
endfunction

module TbMulModule#(type T, Integer n) (Bool verbose);
    T#(n) multiplier;
    Reg#(Bit#(32)) cycle(0);
    Reg#(Bit#(32)) test_count(0);
    Reg#(Bit#(32)) finished_count(0);
    Reg#(Bit#(32)) test_cycles(0);
    Reg#(Bool) multiplier_initialized(False);
    RegU#(MultiplierInput#(n)) current_test;
    GenericRandomizerWrapped#(Bit#(n)) randomA;
    GenericRandomizerWrapped#(Bit#(n)) randomB;

    Bool multiplier_test_done = isValid(multiplier.getResult);

    // Generate test 
    rule testGen;
        let next_test_cycles = test_cycles + 1;
        if (test_count < 128 && cycle > 2) begin
            if (test_count == 0 || multiplier_test_done) begin
                // Start a new test
                let a = randomA.next;
                let b = randomB.next;
                next_test_cycles = 0;
                MultiplierInput#(n) operands;
                operands.a = a;
                operands.b = b;
                current_test <= operands;
                multiplier.in = Valid(operands);
                test_count <= test_count + 1;
            end
        end
        test_cycles <= next_test_cycles;
    endrule

    rule checkTest;
        if (multiplier_test_done) begin
            finished_count <= finished_count + 1;
            let test = fromMaybe(?, multiplier.getResult);
            let a = current_test.a;
            let b = current_test.b;
            let expected = multiply_unsigned_ref(a, b);
            if ( (pack(test == expected) ^ pack(test != expected)) == 0 ) begin
                $display("FAILED undefined value (?) returned");
                $finish;
            end

            if(test != expected) begin
                Int#(n+n) expected_signed = unpack(expected);
                $display("FAILED incorrect result");
                $display("%0d * %0d test function gave %0d instead of %0d", a, b, test, expected);
                $finish;
            end else if (test_cycles > n) begin
                $display("FAILED due to cycle count");
                $display("Took %d cycles instead of %d", test_cycles, n);
                $finish;
            end else if( verbose ) begin
                $display("PASSED case %0d", finished_count);
                $display("%0d * %0d test function gave %0d", a, b, test);
            end
        end
    endrule

    rule initialize;
        if( cycle == 0 ) begin
            randomA.init;
            randomB.init;
        end
    endrule

    rule monitor_test;
        if( finished_count == 128 ) begin
            if( verbose ) begin
                $display("PASSED %0d test cases in %0d cycles", finished_count, cycle);
            end else begin
                $display("PASSED");
            end
            $finish;
        end
        if( cycle == 128*4*n ) begin
            $display("FAILED due to timeout");
            $display("You only completed %d out of 128 tests", finished_count);
            $display("Make sure that your code responds with a valid output within n cycles");
            $finish;
        end
    endrule

    rule count_cycles;
        cycle <= cycle + 1; 
    endrule
endmodule
