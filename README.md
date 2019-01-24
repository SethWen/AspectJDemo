最近写个 Android 项目， 因为好久不谢 Android 了，还真是有些忘了。可能已经蜕变为一只健忘老猿，没跑了。
由于项目里面用到了一些一些地方出现了一些重复代码，看上去很丑，这个时候想到了面向切面的思想，于是乎简单实现了下，简要记录流程。
我的需求如下：我有个 TaskRunner，代码如下. 这个 runner 中有个任务终止标记 `terminate`， 当这个标记值为 `true` 时， 我们就停止执行这个 runner。
所以我们的代买可以写成这个样子，在整个流程的每个方法中都做下 if 判断， `terminate=true`，那我就结束。

    public class TaskRunner {

        private static final String TAG = "TaskRunner";
        private boolean terminate = false;

        public void run() {
            step1();
            step2();
            step3();
        }

        private void step1() {
            if (terminate) return;
            Log.d(TAG, "step1: do it");
        }

        private void step2() {
            if (terminate) return;
            Log.d(TAG, "step2: do it");
        }

        private void step3() {
            if (terminate) return;
            Log.d(TAG, "step3: do it");
        }

        public boolean isTerminate() {
            return terminate;
        }

        public void setTerminate(boolean terminate) {
            this.terminate = terminate;
            Log.d(TAG, "setTerminate: " + terminate);
        }
    }

上面代码简单易懂。但是哈，我就觉得有点傻愣傻愣的。
然后我们面向切面的改一下，代码如下：

        public class TaskRunner {

            private static final String TAG = "TaskRunner";
            private boolean terminate = false;

            public void run() {
                step1();
                step2();
                step3();
            }

            @Terminate
            private void step1() {
                Log.d(TAG, "step1: do it");
            }

            @Terminate
            private void step2() {
                Log.d(TAG, "step2: do it");
            }

            @Terminate
            private void step3() {
                Log.d(TAG, "step3: do it");
            }

            public boolean isTerminate() {
                return terminate;
            }

            public void setTerminate(boolean terminate) {
                this.terminate = terminate;
                Log.d(TAG, "setTerminate: " + terminate);
            }
        }

代码大概类似，只是把流程中的 `if (terminate) return;` 改为一个 `@Terminate` 的注解。别看这个小改动，代码变得简洁的同时，使得这种重复的逻辑不入侵到流程控制中，这种代码本猿还觉得挺美；

下面说下实现过程：

### 1. 添加 gradle 插件及依赖
* 项目根目录  build.gradle 的 buildscript.dependencies 节点中添加:

        classpath 'org.aspectj:aspectjtools:1.8.1'

* 你的主 module(默认是 app) 中添加依赖， 同时引入相关脚本：

        // 依赖
        implementation 'org.aspectj:aspectjrt:1.8.1'

        // 脚本
        import org.aspectj.bridge.IMessage
        import org.aspectj.bridge.MessageHandler
        import org.aspectj.tools.ajc.Main

        final def log = project.logger
        final def variants = project.android.applicationVariants

        variants.all { variant ->
            if (!variant.buildType.isDebuggable()) {
                log.debug("Skipping non-debuggable build type '${variant.buildType.name}'.")
                return
            }

            JavaCompile javaCompile = variant.javaCompile
            javaCompile.doLast {
                String[] args = ["-showWeaveInfo",
                                 "-1.5",
                                 "-inpath", javaCompile.destinationDir.toString(),
                                 "-aspectpath", javaCompile.classpath.asPath,
                                 "-d", javaCompile.destinationDir.toString(),
                                 "-classpath", javaCompile.classpath.asPath,
                                 "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)]
                log.debug "ajc args: " + Arrays.toString(args)

                MessageHandler handler = new MessageHandler(true)
                new Main().run(args, handler)
                for (IMessage message : handler.getMessages(null, true)) {
                    switch (message.getKind()) {
                        case IMessage.ABORT:
                        case IMessage.ERROR:
                        case IMessage.FAIL:
                            log.error message.message, message.thrown
                            break
                        case IMessage.WARNING:
                            log.warn message.message, message.thrown
                            break
                        case IMessage.INFO:
                            log.info message.message, message.thrown
                            break
                        case IMessage.DEBUG:
                            log.debug message.message, message.thrown
                            break
                    }
                }
            }
        }


### 2. 开发注解类
代码如下，如果对注解相关的东西不慎了解，还请稍作补充学习

    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
    public @interface Terminate {
    }

### 3. 开发解释注解的切面类
代码如下，相关解释已在注释中。 AspectJ 的语法我还了解不多， 具体请参照官方文档

    @Aspect
    public class TerminateAspect {
        private static final String TAG = "TerminateAspect";

        // Terminate 切面注解的入口方法
        @Pointcut("execution(@com.shawn.aspectjdemo.aspect.Terminate * *(..))")
        public void cut() {
        }

        @Around("cut()")
        public void onCutAround(ProceedingJoinPoint joinPoint) throws Throwable {
            String key = joinPoint.getSignature().toString();
            TaskRunner runner = (TaskRunner) joinPoint.getThis();
            boolean terminate = runner.isTerminate();
            Log.d(TAG, "onDebugToolMethodAround: " + key + "---" + terminate);
            if (!terminate) {
                joinPoint.proceed();
            }
        }
    }

安卓中使用 AspectJ 实现面向切面的流程也就这样了。 demo 地址在 [这里]()

然后， 然后。。。我要立个 flag 先， 之后介绍下 Python 和 Node.js 中面向切面开发的简要实现。



