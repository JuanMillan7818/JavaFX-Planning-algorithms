/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package so;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.chart.XYChart;
import so.fxmlControllers.DataController;


// FCFS
   /* Condicion para candidato: 
       1. T. Llegada
       2. Por orden lista (Empate)
       3. Ejecucion de cola no apropiativa        
   */

   // Por prioridad
   /* Condiciones para ser candidato:
       1. El que tenga una cantidad la mayor
       2. T. Llegada (Empate)
       3. Por orden de lista (Doble empate)
       4. Ejecucion de cola apropiativa        
   */

/**
 *
 * @author juan
 */
public class Logic {
    private ArrayList<MyProcess> myProcess; // Procesos a calcular         
    public static final int PENALITY = 2; // Penalizacion
    public static int LIMIT; // Total de rafagas de todos los procesos
    public static int TIMECPU; // Tiempo de CPU actual
    public static ArrayList<MyProcess> dataqueue2 = new ArrayList<>(); // Procesos que llegan a la cola 2
    public static ArrayList<MyProcess> dataOrigin = new ArrayList<>(); // Copia de los procesos iniciales para tomar de nuevo informacion
    public static String camino2 = ""; // Saber quien se ha ejecutado en la cola 2
    private boolean activeQueue; // Saber que cola tiene la CPU (true = Cola #1 : false = Cola #2)
    private DataController controller; // Controlador de la interfaz con las graficas

    public Logic() {
        myProcess = new ArrayList<>();    
        activeQueue = false;         
    }

    public ArrayList<MyProcess> getMyProcess() {
        return myProcess;
    }  

    public void setMyProcess(ArrayList<MyProcess> myProcess) {
        this.myProcess = myProcess;
    }
        
    public void start(String queue1, String queue2, DataController controller) {             
        this.controller = controller;
        
        // Copia de datos iniciales
        Logic.dataOrigin = Util.cloneData(myProcess);
        
        // Datos de tiempos ....
        HashMap<String, Integer> allTimeProcess = Util.createHashMap(myProcess);
        
        
        // Ordernar por orden de llegada
        orderByTimeStart();
        
        // Variables auxiliares         
        Logic.TIMECPU = 0;
        Logic.LIMIT = Util.totalBurst(myProcess); // Calcular el numero total de rafagas          
        String camino1 = "", camino2 = "";     
        boolean firstExecution = true; // Validar cuando entre por primera vez a la cola #2
        
        while(Logic.TIMECPU < Logic.LIMIT) {                                      
            ArrayList<MyProcess> candidates = processVisible(Logic.TIMECPU);  // Procesos visibles
            Util.viewData(candidates);
            
            if(candidates.isEmpty()) { // Si no hat candidatos
                Logic.TIMECPU++;
                continue;
            }
            
            if(isEnableProcessForQueue1(candidates)) { // El procesador tiene la cola #1                                
                // Proceso a ejecutar
                MyProcess tmp = Util.adminQueue1(queue1, candidates);                
                                                                                
                // Pintar Ejecucion                                                 
                this.controller.getStateExecution().getData().add(
                        new XYChart.Data<>(Logic.TIMECPU, tmp.getId(), 
                        new GanttChart.ExectutionCPU(1, "status-execute")));                 
                              
               
                // Pintar Espera Cola #1 y Cola #2
                candidates.remove(tmp); // Quito el que se ejecuto   
                waitQueueProcess(Logic.TIMECPU, candidates);                                  
                
                // Pintar Las E/S
                queueES(Logic.TIMECPU, tmp);
                
                // Tomo datos para el resultado
                if(tmp.isExecute()) {
                    allTimeProcess.put(tmp.getId(), Logic.TIMECPU+1);
                    tmp.setTimeFinal(Logic.TIMECPU+1); // Tiempo final
                }
                                                                
                camino1 += tmp.getId();                               
               
            }else { // El procesador tiene la cola #2                                    
                Util.viewData(Logic.dataqueue2);
                // Proceso a ejecutar
                MyProcess tmp = Util.adminQueue2(queue2);                   
                
                if(tmp != null) {                                                                             
                    // Pintar Ejecucion
                    this.controller.getStateExecution().getData().add(
                            new XYChart.Data<>(Logic.TIMECPU, tmp.getId(),
                            new GanttChart.ExectutionCPU(1, "status-execute")));
                    
                    // Pintar Espera Cola #2                    
                    waitQueueProcess(Logic.TIMECPU, Logic.dataqueue2, tmp);
                                        
                    // Pintar Las E/S
                    queueES(Logic.TIMECPU, tmp);
                    
                    // Tomo datos para el resultado
                    if(tmp.isExecute()) {
                        allTimeProcess.put(tmp.getId(), Logic.TIMECPU+1);
                        tmp.setTimeFinal(Logic.TIMECPU+1); // Tiempo final
                    }
                    
                    camino2 += tmp.getId();
                    Logic.camino2 += tmp.getId(); // Agrego el camino de la cola para saber quien fue el ultimo
                }                               
                
            }               
            // Aumento el tiempo de ejecucion
            Logic.TIMECPU++;            
        }
        
        // Retomar los datos de tiempos de espera ....
        Util.joinData(dataOrigin, allTimeProcess); // Limpio datos basura o incorrectos
        this.controller.tableView(dataOrigin);
    }             
    
    public void queueES(int cpu, MyProcess process) {        
        if(process.getBurstStart() == 0 && !process.isExecute() && process.getBurstES() != 0) {            
            this.controller.getStateES().getData().add(
                    new XYChart.Data<>(cpu+1, process.getId(),
                    new GanttChart.ExectutionCPU(process.getBurstES(), "status-ES")));
                       
            process.setBusy(true);
            process.setCounterBusy(process.getBurstES());
            process.setLastExecucion(Logic.TIMECPU+process.getBurstES()+1); // Tiempo de la ultima ejecucion para HRN
            process.setBurstES(0);                        
        }
    }
    
     public void waitQueueProcess(int cpu, 
            ArrayList<MyProcess> candidates) {  
                        
        for(MyProcess item : candidates) {                 
            if(item.isFlagQueue()) { // Espera de la cola 1                
                this.controller.getStateWait1().getData().add(
                    new XYChart.Data<>(cpu,item.getId(),
                    new GanttChart.ExectutionCPU(1, "status-await1")));   
            }else { // Espera de la cola 2                     
                if(!item.isExecute()) {
                    this.controller.getStateWait2().getData().add(
                        new XYChart.Data<>(cpu,item.getId(),
                        new GanttChart.ExectutionCPU(1, "status-await2")));                                                                                   
                }                              
            }            
        }       
    }
    
    public void waitQueueProcess(int cpu, 
            ArrayList<MyProcess> candidates,
            MyProcess current) {  
                        
        for(MyProcess item : candidates) {                 
            if(item.isFlagQueue()) { // Espera de la cola 1                
                this.controller.getStateWait1().getData().add(
                    new XYChart.Data<>(cpu,item.getId(),
                    new GanttChart.ExectutionCPU(1, "status-await1")));   
            }else { // Espera de la cola 2                     
                if(!item.getId().equalsIgnoreCase(current.getId())) {
                    this.controller.getStateWait2().getData().add(
                        new XYChart.Data<>(cpu,item.getId(),
                        new GanttChart.ExectutionCPU(1, "status-await2")));                                                               
                }              
            }            
        }       
    }
    
    public boolean isEnableProcessForQueue1(ArrayList<MyProcess> candidates) {
        if(activeQueue) { // Si la cola #1 tiene la Ejecucion
            for(MyProcess item : candidates) { 
                if(item.isFlagQueue()) { // Valido si existe algun proceso en espera en la Cola #1
                    return true;
                }
            }         
            activeQueue = !activeQueue; // Si no hay nadies en espera en Cola #1 paso la ejecucion a la Cola #2
            return  false;
        }else {
            for(MyProcess item : candidates) {
                if(!item.isFlagQueue()) { // Valido si existe algun proceso en espera en la Cola #2
                    return false;
                }
            } 
            activeQueue = !activeQueue; 
            return true;
        }        
    }
    
    public ArrayList<MyProcess> processVisible(int cpu) { // Devuelve todos los procesos que hasta el momento estan visibles
        ArrayList<MyProcess> aux = new ArrayList<>();
        for(MyProcess item : myProcess) {
            if(item.getArrival() <= cpu) {
                aux.add(item);
            }
        }
        return aux;
    }
    
    public void orderByTimeStart() {      // Ordena por tiempo de llegda mediante algoritmo de burbuja   
        for(int i=0; i<myProcess.size(); i++){
            for(int j=0; j<myProcess.size()-1; j++) {
                if(myProcess.get(j).getArrival() >
                        myProcess.get(j+1).getArrival()) {
                    MyProcess aux = myProcess.get(j+1);
                    myProcess.set(j+1, myProcess.get(j));
                    myProcess.set(j, aux);
                }
            }
        }                       
    }    
        
    public void auxData() { // Funcion para pruebas controladas
        /*
        myProcess.add(new MyProcess("A", 0, 2, 2, 3, 2));
        myProcess.add(new MyProcess("B", 1, 3, 1, 2, 1));
        myProcess.add(new MyProcess("C", 2, 1, 1, 1, 3));
        */
        
        /*
        myProcess.add(new MyProcess("A", 0, 2, 1, 3, 2));
        myProcess.add(new MyProcess("B", 1, 2, 1, 2, 1));
        myProcess.add(new MyProcess("C", 2, 1, 1, 3, 3));
        */
        /*
        myProcess.add(new MyProcess("A", 2, 2, 3, 1, 2));
        myProcess.add(new MyProcess("B", 1, 2, 3, 1, 4));
        myProcess.add(new MyProcess("C", 0, 3, 3, 1, 3));
        myProcess.add(new MyProcess("D", 4, 2, 3, 1, 5));
        myProcess.add(new MyProcess("E", 3, 2, 3, 1, 1));
        */
        /*
        myProcess.add(new MyProcess("A", 0, 2, 2, 3, 0));
        myProcess.add(new MyProcess("B", 2, 1, 2, 2, 1));
        myProcess.add(new MyProcess("C", 1, 3, 1, 3, 0));
        myProcess.add(new MyProcess("D", 4, 2, 3, 2, 2));        
        */
        
        myProcess.add(new MyProcess("A", 2, 3, 2, 2, 0));
        myProcess.add(new MyProcess("B", 3, 2, 2, 4, 1));
        myProcess.add(new MyProcess("C", 5, 1, 2, 1, 0));
        myProcess.add(new MyProcess("D", 6, 3, 2, 2, 2));        
        myProcess.add(new MyProcess("E", 7, 1, 2, 3, 2));
        myProcess.add(new MyProcess("F", 10, 4, 2, 1, 1));
        myProcess.add(new MyProcess("G", 12, 2, 2, 2, 0));   
        /*
        myProcess.add(new MyProcess("H", 0, 4, 2, 3, 0));
        myProcess.add(new MyProcess("I", 3, 2, 2, 2, 1));
        myProcess.add(new MyProcess("J", 3, 5, 2, 2, 0));
        */
        /*
        myProcess.add(new MyProcess("A", 0, 4, 2, 3, 0));
        myProcess.add(new MyProcess("B", 3, 2, 2, 2, 1));
        myProcess.add(new MyProcess("C", 3, 5, 2, 2, 0));
        myProcess.add(new MyProcess("D", 2, 1, 2, 1, 2)); 
        */
        /*
        myProcess.add(new MyProcess("A", 1, 1, 1, 1, 1));
        myProcess.add(new MyProcess("B", 1, 1, 1, 1, 1));
        myProcess.add(new MyProcess("C", 1, 1, 1, 1, 1));
        */
    }      
}
