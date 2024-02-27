package com.barry.common.core.util;

import org.slf4j.Logger;
import org.slf4j.event.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 对{@link java.util.Optional}的增强.
 * {@link java.util.Optional}仅用于解决null带来的一些不便, 但缺少一些常用lambda方法, 并且没有为 String Collection 提供有意义的业务判空能力.
 * {@link Opt}就是为解决该问题而生. 使用Opt,你可以在大部分情况下不中断的在一个流中完成一系列逻辑, 让你的每一行代码都有业务意义的.
 * <p>
 * note: presentPredicate仅用于判空,不要用来做其他逻辑判断.
 * 例如: {@code Opt.ofMaybe("abc", s -> s.equals("ab"))} 会返回一个空的Opt,在后续的流程中,你将会失去对"abc"的引用, 这样做甚至可能让你的代码逻辑混乱, 这不是我们的初衷.
 * </p>
 *
 * @author barry chen
 * @date 2023/6/8 21:40
 */
public class Opt<T> {
    /**
     * Common instance for {@code empty()}.
     */
    private static final Predicate<?> DEFAULT_PRESENT = Objects::nonNull;
    private static final Opt<?> EMPTY = new Opt<>(null);

    /**
     * If non-null, the value; if null, indicates no value is present
     */
    private final T value;
    private final Predicate<T> presentPredicate;
    private Boolean isPresent =null;

    /**
     * Returns an empty {@code Opt} instance.  No value is present for this
     * {@code Opt}.
     *
     * @param <T> The type of the non-existent value
     * @return an empty {@code Opt}
     * @apiNote Though it may be tempting to do so, avoid testing if an object is empty
     * by comparing with {@code ==} or {@code !=} against instances returned by
     * {@code Opt.empty()}.  There is no guarantee that it is a singleton.
     * Instead, use {@link #isEmpty()} or {@link #isPresent()}.
     */
    public static <T> Opt<T> empty() {
        @SuppressWarnings("unchecked")
        Opt<T> t = (Opt<T>) EMPTY;
        return t;
    }

    /**
     * Constructs an instance with the described value.
     *
     * @param value the value to describe; it's the caller's responsibility to
     *              ensure the value is non-{@code null} unless creating the singleton
     *              instance returned by {@code empty()}.
     */
    private Opt(T value) {
        this.value = value;
        this.presentPredicate = (Predicate<T>) DEFAULT_PRESENT;
    }

    /**
     * Constructs an instance with the described value and a present predicate.
     *
     * @param value
     * @param presentPredicate  if present is null, use default present predicate
     */
    private Opt(T value, Predicate<T> presentPredicate) {
        this.value = value;
        this.presentPredicate = presentPredicate ==null? (Predicate<T>) DEFAULT_PRESENT : presentPredicate;
    }

    /**
     * Returns an {@code Opt} describing the given non-{@code null}
     * value.
     *
     * @param value the value to describe, which must be non-{@code null}
     * @param <T>   the type of the value
     * @return an {@code Opt} with the value present
     * @throws NullPointerException if value is {@code null}
     */
    public static <T> Opt<T> of(T value) {
        return new Opt<>(Objects.requireNonNull(value));
    }

    /**
     * Returns an {@code Opt} describing the given value,
     * if present predicate is false, returns an empty Opt.
     *
     * @param value
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> Opt<T> ofMaybe(T value, @Nonnull Predicate<T> predicate) {
        Objects.requireNonNull(predicate);
        return new Opt<>(value, predicate);
    }

    /**
     * Returns an {@code Opt} describing the given value, if
     * non-{@code null}, otherwise returns an empty {@code Opt}.
     *
     * @param value the possibly-{@code null} value to describe
     * @param <T>   the type of the value
     * @return an {@code Opt} with a present value if the specified value
     * is non-{@code null}, otherwise an empty {@code Opt}
     */
    @SuppressWarnings("unchecked")
    public static <T> Opt<T> ofNullable(T value) {
        return Opt.ofMaybe(value, (Predicate<T>) DEFAULT_PRESENT);
    }

    public static Opt<String> ofBlankable(String value) {
        return  Opt.ofMaybe(value, v->!isBlank(v));
    }

    public static <E,C extends Collection<E>> Opt<C> ofEmptyable(C value){
        return Opt.ofMaybe(value, v->!isEmpty(v));
    }

    /**
     * 从 {@link java.util.Optional} 转为 Opt, 默认的present为 Objects::nonNull
      * @param optional
     * @return
     * @param <T>
     */
    public static <T> Opt<T> ofJavaUtil(@Nonnull Optional<T> optional){
        return optional.map(Opt::ofNullable).orElse(Opt.empty());
    }

    /**
     * 从 {@link java.util.Optional} 转为 Opt, 指定present
     *
     * @param optional
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> Opt<T> ofJavaUtil(@Nonnull Optional<T> optional, @Nonnull Predicate<T> predicate) {
        return optional.map(v -> Opt.ofMaybe(v, predicate)).orElse(Opt.empty());
    }

    public Optional<T> toJavaUtil(){
        return Optional.ofNullable(value);
    }

    /**
     *  return a new Opt with this present
     *
     * @param present
     * @return
     */
    public Opt<T> present(@Nonnull Predicate<T> present) {
        return Opt.ofMaybe((T) value, present);
    }

    /**
     * If a value is present, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @apiNote
     * The preferred alternative to this method is {@link #orElseThrow()}.
     *
     * @return the non-{@code null} value described by this {@code Opt}
     * @throws NoSuchElementException if no value is present
     */
    public T get() {
        if (!isPresent()) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    /**
     * If a value is present, returns {@code true}, otherwise {@code false}.
     *
     * @return {@code true} if a value is present, otherwise {@code false}
     */
    public boolean isPresent() {
        if(Objects.nonNull(isPresent)){
            return isPresent;
        }
        isPresent = presentPredicate.test(value);
        return isPresent;
    }

    /**
     * execute this consumer no matter if present
     *
     * @return original Opt (this)
     */
    public boolean isEmpty() {
        return !isPresent();
    }

    /**
     * If a value is present, performs the given action with the value,
     * otherwise does nothing.
     *
     * @param action the action to be performed, if a value is present
     */
    public void ifPresent(Consumer<? super T> action) {
        if (isPresent()) {
            action.accept(value);
        }
    }

    /**
     * If a value is present, performs the given action with the value,
     * otherwise performs the given empty-based action.
     *
     * @param action the action to be performed, if a value is present
     * @param emptyAction the empty-based action to be performed, if no value is
     *        present
     */
    public void ifPresentOrElse(@Nullable Consumer<? super T> action, @Nullable Runnable emptyAction) {
        if (isPresent() && action!=null) {
            action.accept(value);
        } else if(emptyAction!=null){
            emptyAction.run();
        }
    }

    /**
     * if value match predicate,  performs the given action with the value,
     *
     * @param predicate this predicate will not recover the original predicate.
     * @param action
     */
    public void ifMatch(@Nonnull Predicate<T> predicate, @Nullable Consumer<? super T> action) {
        if (isPresent() && predicate.test(value) && action!=null) {
            action.accept(value);
        }
    }

    /**
     * if value match predicate,  performs the given action with the value,
     * otherwise performs the given empty-based action.
     *
     * @param predicate
     * @param action
     * @param noMatchAction
     */
    public void ifMatchOrElse(@Nonnull Predicate<T> predicate, @Nullable Consumer<? super T> action, @Nullable Runnable noMatchAction) {
        if (isPresent() && predicate.test(value) && action!=null) {
            action.accept(value);
        } else if(noMatchAction!=null){
            noMatchAction.run();
        }
    }

    /**
     * If a value is present, and the value matches the given predicate,
     * returns an {@code Opt} describing the value, otherwise returns an
     * empty {@code Opt}.
     *
     * @param predicate the predicate to apply to a value, if present
     * @return an {@code Opt} describing the value of this
     * {@code Opt}, if a value is present and the value matches the
     * given predicate, otherwise an empty {@code Opt}
     * @throws NullPointerException if the predicate is {@code null}
     */
    public Opt<T> filter(@Nonnull Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!isPresent()) {
            return this;
        } else {
            return predicate.test(value) ? this : empty();
        }
    }

    /**
     * If a value is present, returns an {@code Opt} describing (as if by
     * {@link #ofNullable}) the result of applying the given mapping function to
     * the value, otherwise returns an empty {@code Opt}.
     *
     * <p>If the mapping function returns a {@code null} result then this method
     * returns an empty {@code Opt}.
     *
     * @apiNote
     * This method supports post-processing on {@code Opt} values, without
     * the need to explicitly check for a return status.  For example, the
     * following code traverses a stream of URIs, selects one that has not
     * yet been processed, and creates a path from that URI, returning
     * an {@code Opt<Path>}:
     *
     * <pre>{@code
     *     Opt<Path> p =
     *         uris.stream().filter(uri -> !isProcessedYet(uri))
     *                       .findFirst()
     *                       .map(Paths::get);
     * }</pre>
     *
     * Here, {@code findFirst} returns an {@code Opt<URI>}, and then
     * {@code map} returns an {@code Opt<Path>} for the desired
     * URI if one exists.
     *
     * @param mapper the mapping function to apply to a value, if present
     * @param <U> The type of the value returned from the mapping function
     * @return an {@code Opt} describing the result of applying a mapping
     *         function to the value of this {@code Opt}, if a value is
     *         present, otherwise an empty {@code Opt}
     * @throws NullPointerException if the mapping function is {@code null}
     */
    public <U> Opt<U> map(Function<? super T, ? extends U> mapper) {
        return (Opt<U>) map(mapper, (Predicate<U>) DEFAULT_PRESENT);
    }

    /**
     * Mapping to a new Opt, using the presentPredicate of the original Opt
     *
     * @param mapper
     * @return
     */
    public Opt<T> mapSelf(Function<? super T, ? extends T> mapper){
        return map(mapper, this.presentPredicate);
    }

    /**
     * Mapping to a new Opt of String
     * @param mapper
     * @return
     */
    public Opt<String> mapStr(Function<? super T, String> mapper){
        return map(mapper, (String v)-> Opt.ofBlankable(v));
    }

    /**
     * Mapping to a new Opt of Collection
     * @param mapper
     * @return
     * @param <E>
     */
    public <E,C extends Collection<E>> Opt<C> mapCol(Function<? super T, C> mapper){
        return map(mapper, (C v)-> Opt.ofEmptyable(v));
    }

    /**
     * return a new Opt,  the present of the new Opt is {@param newOptPredicate}
     * example
     * <code>
     *     Opt.ofNullable(obj).map(Obj::getXxx, StringUtils::isNotBlank).map(String::upperCase);
     * </code>
     *
     * @param mapper
     * @param newOptPredicate
     * @return
     * @param <U>
     */
    public <U> Opt<U> map(Function<? super T, ? extends U> mapper, Predicate<U> newOptPredicate) {
        return map(mapper, (U u)-> Opt.ofMaybe(u, newOptPredicate));
    }

    /**
     * If a value is present, returns a new Opt which is created by newOptSupplier with mapper's result.
     *
     * @param mapper
     * @param newOptSupplier
     * @return
     * @param <U>
     */
    public <U> Opt<U> map(@Nonnull Function<? super T, ? extends U> mapper, @Nonnull Function<U, Opt<? extends U>> newOptSupplier) {
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(newOptSupplier);
        if (!isPresent()) {
            return empty();
        } else {
            return (Opt<U>) newOptSupplier.apply(mapper.apply(value));
        }
    }

    /**
     * 当满足 present && condition时, 才会执行mapper, 返回一个新的Opt
     * 否则返回一个empty Opt
     *
     * @return
     * @param <U>
     */
    public <U> Opt<U> mapIf(@Nonnull Predicate<T> condition, @Nonnull Function<? super T, ? extends U> mapper) {
        if (isPresent() && condition.test(value)) {
            return map(mapper);
        }
        return empty();
    }

    /**
     * 当满足present时, 才会根据condition来执行mapper/elseMapper
     * 否则返回一个empty Opt
     *
     * @param <U>
     * @return
     */
    public <U> Opt<U> mapIf(@Nonnull Predicate<T> condition, @Nonnull Function<? super T, ? extends U> mapper, @Nonnull Function<? super T, ? extends U> elseMapper) {
        if(!isPresent()){
            return empty();
        }
        if (condition.test(value)) {
            return map(mapper);
        }
        return map(elseMapper);
    }

    /**
     * If a value is present, returns the result of applying the given
     * {@code Opt}-bearing mapping function to the value, otherwise returns
     * an empty {@code Opt}.
     *
     * <p>This method is similar to {@link #map(Function)}, but the mapping
     * function is one whose result is already an {@code Opt}, and if
     * invoked, {@code flatMap} does not wrap it within an additional
     * {@code Opt}.
     *
     * @param <U>    The type of value of the {@code Opt} returned by the
     *               mapping function
     * @param mapper the mapping function to apply to a value, if present
     * @return the result of applying an {@code Opt}-bearing mapping
     * function to the value of this {@code Opt}, if a value is
     * present, otherwise an empty {@code Opt}
     * @throws NullPointerException if the mapping function is {@code null} or
     *                              returns a {@code null} result
     */
    public <U> Opt<U> flatMap(Function<? super T, ? extends Opt<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return empty();
        } else {
            @SuppressWarnings("unchecked")
            Opt<U> r = (Opt<U>) mapper.apply(value);
            return Objects.requireNonNull(r);
        }
    }

    /**
     * execute action if value is not null.
     * note:  not need value is present, instead need value is not null;
     *
     * @param action
     * @return
     */
    public Opt<T> peek(Consumer<? super T> action) {
        if (isPresent() && action != null) {
            action.accept(value);
        }
        return this;
    }

    /**
     * value.toString() will be appended to title's end
     * @param logger
     * @param title
     * @return
     */
    public Opt<T> info(Logger logger, String title) {
        return this.info(logger, title, "{}", v -> v.toString());
    }

    /**
     * args will be appended to title's end
     * @param logger
     * @param title
     * @param argsSupplier
     * @return
     */
    public Opt<T> info(Logger logger, String title, Function<? super T, Object>... argsSupplier) {
        return this.info(logger, title, "{}", argsSupplier);
    }

    public Opt<T> info(Logger logger, String title, String format, Function<? super T, Object>... argsSupplier) {
        return this.log(logger, Level.INFO, title, format, argsSupplier);
    }

    /**
     * value.toString() will be appended to title's end
     * @param logger
     * @param title
     * @return
     */
    public Opt<T> debug(Logger logger, String title) {
        return this.debug(logger, title, "{}", v -> v.toString());
    }

    /**
     * args will be appended to title's end
     * @param logger
     * @param title
     * @param argsSupplier
     * @return
     */
    public Opt<T> debug(Logger logger, String title, Function<? super T, Object>... argsSupplier) {
        return this.debug(logger, title, "{}", argsSupplier);
    }

    public Opt<T> debug(Logger logger, String title, String format, Function<? super T, Object>... argsSupplier) {
        return this.log(logger, Level.DEBUG, title, format, argsSupplier);
    }

    /**
     * @param logger slf4j logger
     * @param level slf4j logger level
     * @param title logger prefix, will output even value is null
     * @param format logger suffix, will output only when value is not null
     * @param argsSupplier format args
     * @return
     */
    public Opt<T> log(Logger logger, Level level, String title, String format, Function<? super T, Object>... argsSupplier) {
        if (Objects.isNull(value)) {
            logger.atLevel(level).log(title + " value is null");
        } else {
            logger.atLevel(level).log(title + " " + format, Arrays.stream(argsSupplier).map(s -> s.apply(value)).toArray());
        }
        return this;
    }

    /**
     * If a value is present, returns an {@code Opt} describing the value,
     * otherwise returns an {@code Opt} describing the new value produced by the supplying function.
     *
     * @param supplier the supplying function that produces an new value to be returned
     * @return returns an {@code Opt} describing the value of this
     *         {@code Opt}, if a value is present, otherwise an
     *         {@code Opt} describing the new value produced by the supplying function.
     * @throws NullPointerException if the supplying function is {@code null} or
     *         produces a {@code null} result
     */
    public Opt<T> or(Supplier<? extends Opt<? extends T>> supplier) {
        Objects.requireNonNull(supplier);
        if (isPresent()) {
            return this;
        } else {
            @SuppressWarnings("unchecked")
            Opt<T> r = (Opt<T>) supplier.get();
            return Objects.requireNonNull(r);
        }
    }

    /**
     * If a value is present, returns the value, otherwise returns
     * {@code other}.
     *
     * @param other the value to be returned, if no value is present.
     *        May be {@code null}.
     * @return the value, if present, otherwise {@code other}
     */
    public T orElse(T other) {
        return isPresent() ? value : other;
    }

    /**
     * If a value is present, returns the value, otherwise returns the result
     * produced by the supplying function.
     *
     * @param supplier the supplying function that produces a value to be returned
     * @return the value, if present, otherwise the result produced by the
     *         supplying function
     * @throws NullPointerException if no value is present and the supplying
     *         function is {@code null}
     */
    public T orElseGet(Supplier<? extends T> supplier) {
        return isPresent() ? value : supplier.get();
    }

    /**
     * If a value is present,  returns an {@code Opt} describing the value. otherwise throws an exception
     * produced by the exception supplying function.
     *
     * @apiNote
     * A method reference to the exception constructor with an empty argument
     * list can be used as the supplier. For example,
     * {@code IllegalStateException::new}
     *
     * @param exceptionSupplier
     * @return
     * @param <X>
     * @throws X
     */
    public <X extends Throwable> Opt<T> orThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (isPresent()) {
            return this;
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * If a value is present, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @return the non-{@code null} value described by this {@code Opt}
     * @throws NoSuchElementException if no value is present
     * @since 10
     */
    public T orElseThrow() {
        if (!isPresent()) {
            throw new NoSuchElementException("value is null or empty or blank");
        }
        return value;
    }

    /**
     * If a value is present, returns the value, otherwise throws an exception
     * produced by the exception supplying function.
     *
     * @apiNote
     * A method reference to the exception constructor with an empty argument
     * list can be used as the supplier. For example,
     * {@code IllegalStateException::new}
     *
     * @param <X> Type of the exception to be thrown
     * @param exceptionSupplier the supplying function that produces an
     *        exception to be thrown
     * @return the value, if present
     * @throws X if no value is present
     * @throws NullPointerException if no value is present and the exception
     *          supplying function is {@code null}
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (isPresent()) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * If a value is present, returns a sequential {@link Stream} containing
     * only that value, otherwise returns an empty {@code Stream}.
     *
     * @apiNote
     * This method can be used to transform a {@code Stream} of Opt
     * elements to a {@code Stream} of present value elements:
     * <pre>{@code
     *     Stream<Opt<T>> os = ..
     *     Stream<T> s = os.flatMap(Opt::stream)
     * }</pre>
     *
     * @return the Opt value as a {@code Stream}
     * @since 9
     */
    public Stream<T> stream() {
        if (!isPresent()) {
            return Stream.empty();
        } else {
            return Stream.of(value);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if(obj instanceof Opt<?>){
            Opt<?> other = (Opt<?>) obj;
            return Objects.equals(value,other.value);
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value != null
                ? ("Opt[" + value + "]")
                : "Opt.empty";
    }

    private boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    private static boolean isBlank(final CharSequence cs) {
        final int strLen = cs == null ? 0 : cs.length();
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static  boolean isEmpty(Collection<?> collection){
        return collection == null || collection.isEmpty();
    }
}
