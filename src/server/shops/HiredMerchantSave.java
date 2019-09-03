package server.shops;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HiredMerchantSave
{
    public static final int NumSavingThreads = 5;
    private static final TimingThread[] Threads;
    private static final AtomicInteger Distribute;
    
    public static void QueueShopForSave(final HiredMerchant hm) {
        final int Current = HiredMerchantSave.Distribute.getAndIncrement() % 5;
        HiredMerchantSave.Threads[Current].getRunnable().Queue(hm);
    }
    
    public static void Execute(final Object ToNotify) {
        for (int i = 0; i < HiredMerchantSave.Threads.length; ++i) {
            HiredMerchantSave.Threads[i].getRunnable().SetToNotify(ToNotify);
        }
        for (int i = 0; i < HiredMerchantSave.Threads.length; ++i) {
            HiredMerchantSave.Threads[i].start();
        }
    }
    
    static {
        Threads = new TimingThread[5];
        for (int i = 0; i < HiredMerchantSave.Threads.length; ++i) {
            HiredMerchantSave.Threads[i] = new TimingThread(new HiredMerchantSaveRunnable());
        }
        Distribute = new AtomicInteger(0);
    }
    
    private static class TimingThread extends Thread
    {
        private final HiredMerchantSaveRunnable ext;
        
        public TimingThread(final HiredMerchantSaveRunnable r) {
            this.ext = r;
        }
        
        public HiredMerchantSaveRunnable getRunnable() {
            return this.ext;
        }
    }
    
    private static class HiredMerchantSaveRunnable implements Runnable
    {
        private static AtomicInteger RunningThreadID;
        private int ThreadID;
        private long TimeTaken;
        private int ShopsSaved;
        private Object ToNotify;
        private ArrayBlockingQueue<HiredMerchant> Queue;
        
        private HiredMerchantSaveRunnable() {
            this.ThreadID = HiredMerchantSaveRunnable.RunningThreadID.incrementAndGet();
            this.TimeTaken = 0L;
            this.ShopsSaved = 0;
            this.Queue = new ArrayBlockingQueue<HiredMerchant>(500);
        }
        
        @Override
        public void run() {
            try {
                while (!this.Queue.isEmpty()) {
                    final HiredMerchant next = this.Queue.take();
                    final long Start = System.currentTimeMillis();
                    next.closeShop(true, false);
                    this.TimeTaken += System.currentTimeMillis() - Start;
                    ++this.ShopsSaved;
                }
                System.out.println("[\u4fdd\u5b58\u96c7\u4f63\u5546\u5e97\u6570\u636e \u7ebf\u7a0b " + this.ThreadID + "] \u5171\u4fdd\u5b58: " + this.ShopsSaved + " | \u8017\u65f6: " + this.TimeTaken + " \u6beb\u79d2.");
                synchronized (this.ToNotify) {
                    this.ToNotify.notify();
                }
            }
            catch (InterruptedException ex) {
                Logger.getLogger(HiredMerchantSave.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        private void Queue(final HiredMerchant hm) {
            this.Queue.add(hm);
        }
        
        private void SetToNotify(final Object o) {
            if (this.ToNotify == null) {
                this.ToNotify = o;
            }
        }
        
        static {
            HiredMerchantSaveRunnable.RunningThreadID = new AtomicInteger(0);
        }
    }
}
