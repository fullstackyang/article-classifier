package com.fullstackyang.nlp.classifier.utils;

import java.util.List;

public class Calculator {

    /**
     * 信息熵
     *
     * @param probabilities
     * @return
     */
    public static double entropy(List<Double> probabilities) {
        // H(X) = -∑P(x∈X)logP(x∈X), H(X|y) = -∑P(x∈X|y)logP(x∈X|y)
        return probabilities.stream().filter(p -> p > 0.0).mapToDouble(p -> -p * Math.log(p)).sum();
    }

    /**
     * 条件信息熵
     *
     * @param probability
     * @param PconditionWithFeature
     * @param PconditionWithoutFeature
     * @return
     */
    public static double conditionalEntrogy(double probability, List<Double> PconditionWithFeature,
                                            List<Double> PconditionWithoutFeature) {
        // H(X|Y) = P(y=1.txt)H(X|y) + P(y=0)H(X|y) 即该特征词出现和不出现两种情况
        return probability * entropy(PconditionWithFeature) + (1 - probability) * entropy(PconditionWithoutFeature);
    }

    /**
     * 卡方检验计算公式
     * @param A
     * @param B
     * @param C
     * @param D
     * @return
     */
    public static double chisquare(int A, int B, int C, int D) {
        // chi = n*(ad-bc)^2.txt/(a+c)*(b+d)*(a+b)*(c+d)
        double chi = Math.log(A + B + C + D) + 2 * Math.log(Math.abs(A * D - B * C))
                - (Math.log(A + C) + Math.log(B + D) + Math.log(A + B) + Math.log(C + D));
        return Math.exp(chi);
    }


    /**
     * 贝叶斯公式计算后验概率 Pc=Pprior*Pcondition<br/>
     * 类条件概率连乘之后过小，故在前面的计算中取对数<br/>
     * 最终结果为log(Pprior)+log(Pcondition)
     *
     * @param Pprior
     * @param Pconditions
     * @return
     */
    public static double Ppost(double Pprior, final List<Double> Pconditions) {
        return Pprior + Pconditions.stream().mapToDouble(Double::valueOf).sum();
    }
}
