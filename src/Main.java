import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        System.out.println(
            mapMerge(
                IntStream.rangeClosed(1, 100),
                List.of(
                    produceIf(then(remainder(3), eq(0)), "Fizz"),
                    produceIf(then(remainder(5), eq(0)), "Buzz")
                ),
                String::concat, Integer::toString
            ).collect(Collectors.joining(" "))
        );
    }

    private static <T> Stream<T> mapMerge(
        IntStream input,
        Collection<? extends IntFunction<? extends Stream<T>>> producers,
        BinaryOperator<T> acc,
        IntFunction<? extends T> fallback
    ) {
        return input
            .mapToObj(x ->
                producers
                    .stream()
                    .flatMap(p -> p.apply(x))
                    .reduce(acc)
                    .orElse(fallback.apply(x))
            );
    }

    private static IntUnaryOperator remainder(int divisor) {
        return x -> x % divisor;
    }

    private static IntPredicate eq(int toValue) {
        return x -> x == toValue;
    }
    
    private static IntPredicate then(IntUnaryOperator op, IntPredicate predicate) {
        return x -> predicate.test(op.applyAsInt(x));
    }

    private static <T> IntFunction<Stream<T>> produceIf(IntPredicate predicate, T value) {
        return x -> Stream.of(value).filter(s -> predicate.test(x));
    }
}
