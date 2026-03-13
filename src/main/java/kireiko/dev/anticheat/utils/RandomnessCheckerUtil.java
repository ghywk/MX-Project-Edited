package kireiko.dev.anticheat.utils;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;

/**
 * 多次次攻击附带其yaw/pitch传入
 * @author kevin, Gemini
 * @since 2026/3/7
 */

public class RandomnessCheckerUtil {
    /**
     * 判断传入的样本是否为以中心数波动的有规律随机数。
     * * @param diffs 长度为20的差值数组，即 diffs[i] = 样本值[i] - 标准值[i]
     * @return true 表示有规律（中心数+随机噪音），false 表示大概率是无规律纯随机
     */
    public boolean isCenteredNoise(float[] diffs) {
        // 保证传入下标0到19共20个样本
        if (diffs == null || diffs.length != 20) {
            return false; // 数量不够
        }

        double[] data = new double[20];
        double[] zeros = new double[20]; // 用于配对检验的0数组
        for (int i = 0; i < 20; i++) {
            data[i] = diffs[i];
            zeros[i] = 0.0;
        }

        DescriptiveStatistics stats = new DescriptiveStatistics(data);
        double mean = stats.getMean();
        double std = stats.getStandardDeviation();
        double median = stats.getPercentile(50);

        int score = 0;

        // Wilcoxon 符号秩检验 (满分 40分)
        WilcoxonSignedRankTest wilcoxon = new WilcoxonSignedRankTest();
        try {
            double pWilcoxon = wilcoxon.wilcoxonSignedRankTest(data, zeros, true);
            // P值越大，越不能拒绝它是“以0为中心”的假设
            if (pWilcoxon > 0.1) {
                score += 40;
            } else if (pWilcoxon > 0.05) {
                score += 20;
            }
        } catch (Exception e) {
            // 如果所有差值全是绝对的0（极其完美的数据），库可能会抛出异常，直接给满分
            score += 40;
        }

        // Kolmogorov-Smirnov 检验
        if (std > 0) {
            KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();
            // 假设其为一个基于当前均值和方差的正态分布
            NormalDistribution normalDist = new NormalDistribution(mean, std);
            double pKS = ksTest.kolmogorovSmirnovTest(normalDist, data);

            // 即使是普通均分随机数(Uniform)，只要范围紧凑，KS检验对N=20的小样本往往也能拿到一定的P值
            if (pKS > 0.1) {
                score += 30;
            } else if (pKS > 0.05) {
                score += 15;
            }
        } else {
            // 方差为0说明数据完全一致，肯定是某种严格设定的规律数
            score += 30;
        }

        // IQR 与 MAD 比值检查
        double q1 = stats.getPercentile(25);
        double q3 = stats.getPercentile(75);
        double iqr = q3 - q1;

        // 计算 MAD (中位数绝对偏差)
        double[] absDeviations = new double[20];
        for (int i = 0; i < 20; i++) {
            absDeviations[i] = Math.abs(data[i] - median);
        }
        DescriptiveStatistics devStats = new DescriptiveStatistics(absDeviations);
        double mad = devStats.getPercentile(50);

        if (mad == 0) {
            score += 30; // 极度聚集，必定是有中心规律的
        } else {
            double iqrMadRatio = iqr / mad;
            // 正态分布和均匀分布的 IQR/MAD 理论上都在 2.0 左右。
            // 设定一个宽容区间，排除纯离散的无规律数据
            if (iqrMadRatio >= 1.2 && iqrMadRatio <= 3.5) {
                score += 30;
            }
        }

        return score >= 60;
    }
}