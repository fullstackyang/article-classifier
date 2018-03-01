# article-classifier
基于朴素贝叶斯实现的一款微信公众号文章分类器
## 运行环境
- 本项目需要在Java 8环境下运行
- 项目根目录下data/是用来存放训练好的模型文件，以及停用词表，当然你可以根据需要修改这些路径，模型文件路径在naivebayes.NaiveBayesModels中修改，停用词表路径在utils.MyStopWord中修改
- 关于分词器
  - 系统中使用了两个分词器，Ansj（默认）[https://github.com/NLPchina/ansj_seg]和HanLp[https://github.com/hankcs/HanLP]， 这里表示感谢。
  - 根目录下的library/是Ansj所需要的文件
  - 如有其他分词，则可以实现NLPTools中的接口，并在实现时转化为Term对象，同时确认已经实现了统计词频的功能，另外词性是用来过滤噪声词的，若分词器未提供词性标注功能，默认可以全部标注为n，然后使用其他过滤方法。
  
## 如何训练
训练集的目录结构如下：
```
trainset
  |--养生
    |--文档1
    |--文档2
  |--历史
    |--文档3
    |--文档4
    |--文档4
  ...
  |--游戏
    ...
```
每个子目录的名称将被取出，定义为类别的名称

训练模型时调用naivebayes.NaiveBayesLearner的主函数即可，传入训练集的路径，同时可以设定特征选择的方法（ChiSquaredStrategy或IGStrategy），以及朴素贝叶斯的模型（Bernoulli和Multinomial）
```
 public static void main(String[] args) {
        TrainSet trainSet = new TrainSet(System.getProperty("user.dir") + "/trainset/");

        log.info("特征选择开始...");
        FeatureSelection featureSelection = new FeatureSelection(new ChiSquaredStrategy(trainSet.getCategorySet(), trainSet.getTotalDoc()));
        List<Feature> features = featureSelection.select(trainSet.getDocs());
        log.info("特征选择完成,特征数:[" + features.size() + "]");

        NaiveBayesModels model = NaiveBayesModels.Multinomial;
        NaiveBayesLearner learner = new NaiveBayesLearner(model, trainSet, Sets.newHashSet(features));
        learner.statistics().build().write(model.getModelPath());
        log.info("模型文件写入完成,路径:" + model.getModelPath());
    }
```
训练时，根据需要调整JVM参数-Xmx，笔者的训练集大于3万篇文档，设置-Xmx2000m，训练结束时，模型文件生成到data/目录下，目前提交了一个已经训练好的模型文件，可以直接使用。

## 如何预测分类
初始化分类器，调用predict方法
```
    @Test
    public void test() {
        NaiveBayesClassifier classifier = new NaiveBayesClassifier(NaiveBayesModels.Multinomial);
        String text = "明日赛事推荐：切尔西巴萨冤家路窄，恒大申花再战亚冠";
        String category = classifier.predict(text);
        System.out.println(category);
    }
```