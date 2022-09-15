package com.example.study.simple.design.builder;

public class Jacket {
        int number;
        String name;
        double size;

        private Jacket(int number, String name, double size) {
            //Jacket 클래스 초기화
            this.number = number;
            this.name = name;
            this.size = size;
        }

        // 빌더 클래스는 인스턴스를 생성자를 통해 직접 생성하지 않고,
        // 빌더라는 내부 클래스를 통해 간접적으로 생성하게 하는 패턴이다
        public static class Builder {
            int number=0;
            String name=null;
            double size=0d;

            public Builder() {
                //Builder 초기화
            }

            public Builder setNumber(int number) {
                this.number = number;
                return this;
            }

            public Builder setName(String name) {
                this.name = name;
                return this;
            }

            public Builder setSize(double size) {
                this.size = size;
                return this;
            }

            public Jacket build() {
                return new Jacket(number, name, size);
            }
        }
}
