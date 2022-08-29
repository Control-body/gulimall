package com.atguigu.gulimall.search.thread;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/9 9:39
 *
 * @author Control.
 * @since JDK 1.8
 */

import java.util.concurrent.*;

/**
 * 初始化线程的4种方式：
 * 1. 继承Thread
 * 2. 实现Runnable接口
 * 3. 实现Callable接口 + FutureTask （可以拿到异步执行的返回结果，可以处理异常）
 * 4. 线程池
 */
public class ThreadTest {
    //        每个系统中都只有一两个 线程池 进
    public static ExecutorService executorService = Executors.newFixedThreadPool(10);
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        /**
         *  1. 继承Thread
         *         System.out.println("main....start");
         *         Thread01 thread01 = new Thread01();
         *         thread01.start();// 启动线程
         *         System.out.println("main....stop");
         *  * 2. 实现Runnable接口  可以拿到异步执行的返回结果，可以处理异常） 使用
         *   public FutureTask(Runnable runnable, V result) { 的这个构造方法 传入第二个值 用于返回结果
         *      System.out.println("main....start");
         *         Thread02 thread02 = new Thread02();
         *         Thread thread = new Thread(thread02);
         *         thread.start();
         *         System.out.println("main....stop");
         *  * 3. 实现Callable接口 + FutureTask （可以拿到异步执行的返回结果，可以处理异常）
         *          System.out.println("main....start");
         *         Thread03 thread03 = new Thread03();
         *         FutureTask<Integer> futureTask = new FutureTask<>(thread03);
         *         Thread thread = new Thread(futureTask);
         *         thread.start();
         * //        阻塞等等待当 执行完成之后 才会 继续运行主线程
         *         Integer o =futureTask.get();
         *         System.out.println("返回的结果是"+o);
         *         System.out.println("main....stop");
         *  * 4. 线程池
         *   为啥使用线程池？
         *    当每次业务 都 new start  ，肯定会将资源耗尽 ，必须使用线程池 以上三种都不行
         *    以后将异步任务都交给线程池 进行处理业务
         *    execute  和 submit 的区别:   execute 是直接执行,submit 是有返回值
         *
         *
         *
         *     public static ExecutorService executorService = Executors.newFixedThreadPool(10);
         *            executorService.execute(new Thread01() {});
         *
         */
//        每个系统中都只有一两个 线程池 进
        System.out.println("main....start");
//         使用 executorService 第二个参数 设置那个 线程池进行处理
//        CompletableFuture.runAsync(()->{
//            System.out.println("当前线程"+Thread.currentThread().getId());
//            int i=10/2;
//            System.out.println("运行结果"+i);
//        },executorService);

//        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果" + i);
//            return i;
//        }, executorService);
//        System.out.println(integerCompletableFuture.get());
//        System.out.println("main....stop");
        /**
         * 方法完成后的感知
         */
//		CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//			System.out.println("当前线程" + Thread.currentThread().getId());
//			int i = 10 / 0;
//			System.out.println("运行结束" + i);
//			return i;
//		}, executorService).whenComplete((result , ex) -> {
//			// 这里能获取异常信息 但是没法修改数据
//			System.out.println("异步任务成功完成了... 结果:" + result);
//			// 感知异常 给出默认结果
//		}).exceptionally(ex -> 10);
//        System.out.println(future.get());
/**
 * 方法执行完成后的处理
 */
//		CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//			System.out.println("当前线程" + Thread.currentThread().getId());
//			int i = 10 / 0;
//			System.out.println("运行结束" + i);
//			return i;
//		}, executorService).handle((result, ex) -> {
//			if(result != null){
//				return result * 8;
//			}
//			if(ex != null){
//				System.out.println("异常为:" + ex);
//				return -1;
//			}
//			return 0;
//		});
//		System.out.println("main....end 结果：" + future.get());

        /**
         * 线程串行化
         */
//		CompletableFuture.supplyAsync(() -> {
//			System.out.println("当前线程" + Thread.currentThread().getId());
//			int i = 10 / 2;
//			System.out.println("运行结束" + i);
//			return i;
//		}, executorService).thenRunAsync(() -> {
//			// thenRunAsync 不能获取执行结果
//			System.out.println("任务2启动了..."+Thread.currentThread().getId());
//		},executorService);

        /**
         * 使用上一步的结果 但是没有返回结果
         */
//		CompletableFuture.supplyAsync(() -> {
//			System.out.println("当前线程" + Thread.currentThread().getId());
//			int i = 10 / 2;
//			System.out.println("运行结束" + i);
//			return i;
//		}, executorService).thenAcceptAsync(res -> {
//            System.out.println("任务2启动了..."+Thread.currentThread().getId());
//            System.out.println("thenAcceptAsync获取上一步执行结果：" + res);
//        });

        /**
         * 能接受上一步的结果 还有返回值
         */
//		CompletableFuture<String> async = CompletableFuture.supplyAsync(() -> {
//			System.out.println("当前线程" + Thread.currentThread().getId());
//			int i = 10 / 2;
//			System.out.println("运行结束" + i);
//			return i;
//		}, executorService).thenApplyAsync(res -> {
//			System.out.println("任务2启动了...");
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			return "thenApplyAsync" + res;
//		});
//		System.out.println("thenApplyAsync获取结果:" + async.get());

        /**
         * 两任务合并
         */
//		CompletableFuture<Object> async1 = CompletableFuture.supplyAsync(() -> {
//			System.out.println("任务1线程" + Thread.currentThread().getId());
//			int i = 10 / 2;
//			System.out.println("任务1结束" + i);
//			return i;
//		}, executorService);
//
//		CompletableFuture<Object> async2 = CompletableFuture.supplyAsync(() -> {
//			System.out.println("任务2线程" + Thread.currentThread().getId());
//			int i = 10 / 2;
//			try {
//				Thread.sleep(5000);
//                System.out.println("任务2结束" + i);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//
//			return "任务合并";
//		}, executorService);

//         合并上面两个任务 这个不能感知结果
//		async1.runAfterBothAsync(async2,() ->{
//			System.out.println("任务3开始...");
//		} ,executorService);

        // 合并上面两个任务 可以感知前面任务的结果
//		async1.thenAcceptBothAsync(async2,(res1, res2) -> {
//			System.out.println("任务3开始... 任务1的结果：" + res1 + "任务2的结果：" + res2);
//		},service);

        /**
         * 合并两个任何 还可以返回结果
         */
//		CompletableFuture<String> async = async1.thenCombineAsync(async2, (res1, res2) -> res1 + ":" + res2 + "-> fire", service);
//		System.out.println("自定义返回结果：" + async.get());

        /**
         * 合并两个任务 其中任何一个完成了 就执行这个 不感知结果
         */
//		async1.runAfterEitherAsync(async2, () ->{
//
//			System.out.println("任务3开始...之前的结果:");
//		},executorService);

        /**
         * 感知结果 自己没有返回值
         */
//		async1.acceptEitherAsync(async2, (res)-> System.out.println("任务3开始...之前的结果:" + res), executorService);

        /**
         * 感知结果 自己有返回值
         */
//		CompletableFuture<String> async = async1.applyToEitherAsync(async2, (res) -> {
//
//			System.out.println("任务3开始...之前的结果:" + res);
//			return res.toString() + "-> fire";
//		}, executorService);
//		System.out.println("任务3返回的结果：" + async.get());
//

        /**
         * 多任务 整合
         */
        CompletableFuture<String> img = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品图片信息");
            return "1.jpg";
        },executorService);
//
        CompletableFuture<String> attr = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
                System.out.println("查询商品属性");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return "麒麟990 5G  钛空银";
        },executorService);


        CompletableFuture<String> desc = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品介绍");
            return "华为";
        },executorService);

        /**
         * 等这三个都做完
         */
		CompletableFuture<Void> allOf = CompletableFuture.allOf(img, attr, desc);
//		allOf.join();
//        get 阻塞式等待
//        allOf.get();
//		System.out.println("main....end"  + desc.get() + attr.get() + img.get());

        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(img, attr, desc);
        anyOf.get();

        System.out.println("main....end" + anyOf.get());
//        service.shutdown();
    }

    /**
     * 1. 继承Thread
     */
    public static class Thread01 extends Thread{
        @Override
        public void run() {
            System.out.println("当前线程"+Thread.currentThread().getId());
            int i=10/2;
            System.out.println(
                    "运行结果"+i

            );
        }
    }
    /**
     * 2. 实现Runnable接口
     */
    public static  class Thread02 implements Runnable{

        @Override
        public void run() {
            System.out.println("当前线程"+Thread.currentThread().getId());
            int i=10/2;
            System.out.println(
                    "运行结果"+i

            );
        }
    }
    /**
     * 3.实现Callable接口 + FutureTask （
     */
    public static  class Thread03 implements Callable<Integer>{

        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程"+Thread.currentThread().getId());
            int i=10/2;
            System.out.println("运行结果"+i);
            return i;
        }
    }
}
