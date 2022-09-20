package com.example.study.simple.code.effective;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 아이템 45. 스트림은 주의해서 사용하라. + 기타 스트림 잡다한 내용 표기
 */
public class StreamStudy {
    private List<String> myList = new ArrayList<>();

    /**
     * 스트림 기본 구조
     */
    public void streamPipeLine() {
        myList.stream() // 소스 스트림
                .filter(s -> s.startsWith("c")) // 중간 연산
                .forEach(System.out::println); // 종단 연산
    }

    /**
     * computeIfAbsent
     *  - Map에서 특정 key 값이 존재하는지 확인 후 있으면 해당 value 반환.
     *  - 없을 경우 새로 만들어주는 패턴
     */
    public void computeIfAbsent() {
        Map<String, Integer> map = new HashMap<>();
        map.put("John", 5);

        int value = map.computeIfAbsent("John", key -> key.length()); // 존재하면 value값을 반환함. 존재하지 않으면 key.length 반환
    }

    /**
     * 올바르지 않은 stream 사용
     *  - 아래 내용은 스트림 코드를 가장한 반복코드 블럭임.
     */
    public void incorrectLoopStream(File file) throws FileNotFoundException {
        Map<String, Long> freq = new HashMap<>();
        try(Stream<String> words = new Scanner(file).tokens()){
           words.forEach(word -> {
               freq.merge(word.toLowerCase(), 1L, Long::sum);
           });
        }
    }

    /**
     * 올바른 stream 사용
     */
    public void correctLoopStream(File file) throws FileNotFoundException {
        Map<String, Long> freq = new HashMap<>();
        try(Stream<String> words = new Scanner(file).tokens()){
            freq = words.collect(Collectors.groupingBy(String::toLowerCase, Collectors.counting()));
        }
    }

    /**
     * partitioningBy 예제
     *  - 분류함수 자리에 Predicate를 받으며 Map의 Key 타입이 Boolean임.
     */
    public void partitioningByEx() {
        Stream<String> stream = Stream.of("HTML", "CSS", "JAVA", "PHP");

        Map<Boolean, List<String>> partition = stream.collect(Collectors.partitioningBy(s -> (s.length() & 2 ) == 0));

        List<String> oddLengthList = partition.get(false);
        List<String> evenLengthList = partition.get(true);
    }

    /**
     * groupingBy 예제
     */
    public void groupingByEx() {
        List<Product> productList = Arrays.asList(new Product(23, "potatoes"),
                new Product(14, "orange"),
                new Product(13, "lemon"),
                new Product(23, "break"),
                new Product(13, "sugar"));

        Map<Integer, List<Product>> collectorMapOfLists = productList.stream()
                .collect(Collectors.groupingBy(Product::getAmount));
    }

    /**
     * map 예제
     *  - map은 스트림 내부 요소에 하나씩 접근해서 파라미터로 넣어준 함수 실행 후 최종연산에서 지정한 형식으로 반환.
     */
    public void mapEx() {
        List<Product> productList = Arrays.asList(new Product(23, "potatoes"),
                new Product(14, "orange"),
                new Product(13, "lemon"),
                new Product(23, "break"),
                new Product(13, "sugar"));

        List<String> amountList = productList.stream().map(product -> product.getName()).collect(Collectors.toList());
    }

    /**
     * flatMap 예제
     *  - 중복된 스트림을 1차원 배열로 평면화시키는 예제
     */
    public void flatMapEx() {
        String[] animal = new String[]{"cat", "dog"};

        List<String> result = Arrays.stream(animal).map(ani -> ani.split("")).flatMap(Arrays::stream).collect(Collectors.toList());
        // result = [c,a,t,d,o,g]
    }

    class Product {
        int amount;
        String name;

        public Product(int amount, String name) {
            this.amount = amount;
            this.name = name;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
};
