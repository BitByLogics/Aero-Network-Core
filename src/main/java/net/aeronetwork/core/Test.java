package net.aeronetwork.core;

import lombok.AllArgsConstructor;
import net.aeronetwork.core.util.TimeConverter;

public class Test {

    public static void main(String[] args) {
//        MorphiaService service = new MorphiaService("aero_players", AeroPlayer.class);
//        System.out.println(service.getClient().getAddress());
//
//        List<MongoCredential> credentials = new ArrayList<>(
//                Arrays.asList(MongoCredential.createCredential("root", "admin", "aeroiscool!".toCharArray()))
//        );
//        MongoClient client = new MongoClient(new ServerAddress("162.251.166.138", 12345), credentials);
//        client.listDatabaseNames().forEach(new Block<String>() {
//            @Override
//            public void apply(String s) {
//                System.out.println(s);
//            }
//        });
//        System.out.println(TimeConverter.convert("1y 2mh 3w 4d 13m 32s"));

//        ExpManager manager = new ExpManager();
//        System.out.println(manager.getExperienceForLevel(1));
//        System.out.println(manager.getExperienceForLevel(2));
//        System.out.println(manager.getExperienceForLevel(3));
//        System.out.println(manager.getExperienceForLevel(4));
//        System.out.println(manager.getExperienceForLevel(5));
//        System.out.println(" ");
//        System.out.println(manager.calculateLevel(205));
//
//        System.out.println(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30));
//        System.out.println(165 - ((165 / 60) * 60));

//        Enum<? extends ITest> test = ETest.E_TEST;
//
//        try {
//            Method method = test.getClass().getMethod("getName");
//            System.out.println(method.invoke(test));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Executors.newSingleThreadExecutor().submit(() -> {
//            TimingsUtil.start("EH");
//            try {
//                Thread.sleep(2031);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            TimingsUtil.end("EH");
//        });

        System.out.println(TimeConverter.convertToReadableTime(TimeConverter.convert("65s 25h 32d 2y")));
    }

    public interface ITest {

        String getName();
    }

    public interface ITest2 {

    }

    @AllArgsConstructor
    public enum ETest implements ITest {
        E_TEST("some name");

        private String name;

        @Override
        public String getName() {
            return name;
        }
    }

    public class Testt implements ITest, ITest2 {

        @Override
        public String getName() {
            return null;
        }
    }
}
