package com.allen.component.common.oss;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 目录列表类，实现了IteratorAggregate接口。
 *
 * @param <T> 列表元素的类型
 */
public class DirectoryListing<T> implements Iterable<T> {

    private final Iterable<T> listing;

    /**
     * 构造方法。
     *
     * @param listing 列表数据
     */
    public DirectoryListing(Iterable<T> listing) {
        this.listing = listing;
    }

    /**
     * 过滤列表中的元素。
     *
     * @param filter 过滤器函数
     * @return 过滤后的新目录列表
     */
    public DirectoryListing<T> filter(Predicate<T> filter) {
        Stream<T> stream = StreamSupport.stream(listing.spliterator(), false).filter(filter);
        return new DirectoryListing<>(stream::iterator);
    }

    /**
     * 将列表中的元素进行映射。
     *
     * @param <R>    目标类型
     * @param mapper 映射函数
     * @return 映射后的新目录列表
     */
    public <R> DirectoryListing<R> map(Function<T, R> mapper) {
        Stream<R> stream = StreamSupport.stream(listing.spliterator(), false).map(mapper);
        return new DirectoryListing<>(stream::iterator);
    }

    /**
     * 按照路径对目录列表进行排序。
     *
     * @return 排序后的新目录列表
     */
    public DirectoryListing<T> sortByPath() {
        Object[] array = StreamSupport.stream(listing.spliterator(), false).toArray();
        Arrays.sort(array, Comparator.comparing(Object::toString));
        return new DirectoryListing<>(Arrays.stream(array).map(obj -> (T) obj)::iterator);
    }

    /**
     * 获取迭代器。
     *
     * @return 迭代器对象
     */
    @Override
    public Iterator<T> iterator() {
        return listing.iterator();
    }

    /**
     * 将目录列表转换为数组。
     *
     * @return 目录列表的数组表示形式
     */
    public Object[] toArray() {
        return StreamSupport.stream(listing.spliterator(), false).toArray();
    }

}
