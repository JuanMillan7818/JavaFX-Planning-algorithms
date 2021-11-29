/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package so;

/**
 *
 * @author juan
 */
public class MyProcess implements Cloneable {
    private String id;
    private int arrival;
    private int burstStart;
    private int burstES;
    private int burstEnd;
    private int priority;
   
    // Datos resultantes para la tabla
    private int timeFinal;
    private int timeService;
    private int timeInstant;
    private float indexService;    
   
    // Variables auxilares
    private int burstExecute;
    private boolean flagQueue;
    private boolean execute;
    private boolean busy;
    private int counterBusy;
    private int lastExecucion;
   

    public MyProcess() {
    }

    public MyProcess(String id, int arrival, int burstStart, int burstES, int burstEnd, int priority) {
        this.id = id;
        this.arrival = arrival;
        this.burstStart = burstStart;
        this.burstES = burstES;
        this.burstEnd = burstEnd;
        this.priority = priority;
        this.burstExecute = 0;
        flagQueue = true;
        execute = false;
        busy = false;
        counterBusy = -1;
        timeFinal = 0;
        timeService = 0;
        timeInstant = 0;
        indexService = 0;
    }  

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getArrival() {
        return arrival;
    }

    public void setArrival(int arrival) {
        this.arrival = arrival;
    }

    public int getBurstStart() {
        return burstStart;
    }

    public void setBurstStart(int burstStart) {
        this.burstStart = burstStart;
    }

    public int getBurstES() {
        return burstES;
    }

    public void setBurstES(int burstES) {
        this.burstES = burstES;
    }

    public int getBurstEnd() {
        return burstEnd;
    }

    public void setBurstEnd(int burstEnd) {
        this.burstEnd = burstEnd;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getBurstExecute() {
        return burstExecute;
    }

    public void setBurstExecute(int burstExecute) {
        this.burstExecute = burstExecute;
    }        

    public boolean isFlagQueue() {
        return flagQueue;
    }

    public void setFlagQueue(boolean flagQueue) {
        this.flagQueue = flagQueue;
    }

    public boolean isExecute() {
        return execute;
    }

    public void setExecute(boolean execute) {
        this.execute = execute;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public int getCounterBusy() {
        return counterBusy;
    }

    public void setCounterBusy(int counterBusy) {
        this.counterBusy = counterBusy;
    } 

    public int getTimeFinal() {
        return timeFinal;
    }

    public void setTimeFinal(int timeFinal) {
        this.timeFinal = timeFinal;
    }

    public int getTimeService() {
        return timeService;
    }

    public void setTimeService(int timeService) {
        this.timeService = timeService;
    }

    public int getTimeInstant() {
        return timeInstant;
    }

    public void setTimeInstant(int timeInstant) {
        this.timeInstant = timeInstant;
    }

    public float getIndexService() {
        return indexService;
    }

    public void setIndexService(float indexService) {
        this.indexService = indexService;
    }

    public int getLastExecucion() {
        return lastExecucion;
    }

    public void setLastExecucion(int lastExecucion) {
        this.lastExecucion = lastExecucion;
    }       
    
    @Override
    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MyProcess{id=").append(id);
        sb.append(", arrival=").append(arrival);
        sb.append(", burstStart=").append(burstStart);
        sb.append(", burstES=").append(burstES);
        sb.append(", burstEnd=").append(burstEnd);
        sb.append(", priority=").append(priority);
        sb.append(", timeFinal=").append(timeFinal);
        sb.append(", timeService=").append(timeService);
        sb.append(", timeInstant=").append(timeInstant);
        sb.append(", indexService=").append(indexService);
        sb.append(", burstExecute=").append(burstExecute);
        sb.append(", flagQueue=").append(flagQueue);
        sb.append(", execute=").append(execute);
        sb.append(", busy=").append(busy);
        sb.append(", counterBusy=").append(counterBusy);
        sb.append(", lastExecucion=").append(lastExecucion);
        sb.append('}');
        return sb.toString();
    }

         
    
}
