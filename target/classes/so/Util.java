/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package so;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import so.fxmlControllers.DataController;

/**
 *
 * @author juan
 */
public class Util {
    
    public static MyProcess adminQueue1(String type, ArrayList<MyProcess> candidates){        
        switch(type) {
            case " Por Prioridad": {                 
                // Proceso a ejecutar
                MyProcess tmp = orderBypriority(candidates);                                               
                if(tmp != null) { // Si hay candidato valido                    
                    if(tmp.getBurstStart() != 0) { // Rafagas restantes de la primera ola                    
                        tmp.setBurstStart(tmp.getBurstStart()-1); // Quito una rafaga
                        tmp.setBurstExecute(tmp.getBurstExecute()+1); // Rafagas ejecutadas                    
                        if(tmp.getBurstExecute() == Logic.PENALITY) { // Cambio de cola         
                            tmp.setBurstExecute(0); // Reinicio las rafagas ejecutadas
                            tmp.setFlagQueue(!tmp.isFlagQueue()); // Cambio de cola al proceso                    
                            Logic.dataqueue2.add(tmp); // Lo agrego a los procesos de la cola #2                            
                        }                    
                    }else { // Cambio de rafaga                                            
                        if(tmp.getBurstEnd() != 0) { // Si no ha acabo sus rafagas
                            tmp.setBurstEnd(tmp.getBurstEnd()-1); // Quito una rafaga
                            tmp.setBurstExecute(tmp.getBurstExecute()+1); // Rafagas ejecutadas 
                            if(tmp.getBurstExecute() == Logic.PENALITY) { // Cambio de cola
                                tmp.setBurstExecute(0); // Reinicio las rafagas ejecutadas
                                tmp.setFlagQueue(!tmp.isFlagQueue());  // Cambio de cola al proceso                     
                                Logic.dataqueue2.add(tmp);                                
                                if(tmp.getBurstEnd() == 0) tmp.setExecute(true); // Verifico si ya acabo
                            }                    
                        }
                    }                    
                    if(Logic.TIMECPU == Logic.LIMIT-1) { // Si es la ultima rafaga total de todos
                        if(tmp.getBurstEnd() != 0 || candidates.size() >= 1) { // Si no ha acado de ejecutar todo
                            Logic.LIMIT++; // En el caso especial de desfase
                            DataController.xAxis.setUpperBound(DataController.xAxis.getUpperBound()+1); // Agrego una ejecucion mas
                        }
                    } 
                }else { // Si en ese momento no hay candidatos
                     if(!candidates.isEmpty()) { // Si hay alguien por ejecutar luego
                        tmp = candidates.get(0); // Tomo el primero como base
                        if(Logic.TIMECPU == Logic.LIMIT-1) { // Si es la ultima ejecucion
                            if(tmp.getBurstEnd() != 0 || candidates.size() >= 1) { // Si aun queda alguien por ejecutar
                                Logic.LIMIT++; // En el caso especial de desfase
                                DataController.xAxis.setUpperBound(DataController.xAxis.getUpperBound()+1); // Agrego una ejecucion mas
                            }
                        }  
                    }
                }                                
                freeProcess(candidates, false); // Libero procesos que ya acabaron con E/S                
                tmp.setLastExecucion(Logic.TIMECPU+1); // Tiempo de la ultima ejecucion para HRN                
                return tmp;                
            }
            default: {
                Notification notification = new Notification("No coincide ningun algoritmo", 
                        "Por favor verifica que elegiste un algoritmo valido");                
            }
        }                
        return null;
    }
    
    public static MyProcess adminQueue2(String type){            
        switch(type) {
            case " FCFS": {  // Algoritmo FCFS
                //freeProcess(Logic.dataqueue2);                                
                // Proceso a ejecutar
                MyProcess tmp = algorithmFCFS(Logic.dataqueue2);                  
                if(tmp != null) {                         
                    if(tmp.getBurstStart() != 0) { // Rafagas iniciales restantes                    
                        tmp.setBurstStart(tmp.getBurstStart()-1);  // Quito una rafaga
                        tmp.setBurstExecute(tmp.getBurstExecute()+1); // Rafagas ejecutadas                       
                    }else { // Cambio de rafaga                                
                        if(tmp.getBurstEnd() != 0) { // Si no ha acabo sus rafagas
                            tmp.setBurstEnd(tmp.getBurstEnd()-1); // Quito una rafaga
                            tmp.setBurstExecute(tmp.getBurstExecute()+1); // Rafagas ejecutadas 
                            if(tmp.getBurstEnd() == 0) tmp.setExecute(true); // Verifico si ya acabo                                                                         
                        }
                    }                    
                    if(tmp.isExecute()) { // Si ya acabo lo quito de candidatos
                        Logic.dataqueue2.remove(tmp);                        
                    }       

                    // Si aun le queda para ejecutar                         
                    if(tmp.getBurstStart() == 0 && tmp.getBurstES() != 0) { // Si no ha hecho su E/S
                        try {                                  
                            if(Logic.dataqueue2.size() != 1) { // Si no queda solo un proceso                                
                                MyProcess dataCopy = (MyProcess) tmp.clone();
                                dataCopy.setBurstES(0);
                                Logic.dataqueue2.add(dataCopy); // Lo paso al final de la cola
                                Logic.dataqueue2.remove(tmp);
                            }else {                                
                                Logic.LIMIT++; // En el caso especial de desfase
                                DataController.xAxis.setUpperBound(DataController.xAxis.getUpperBound()+1); // Agrego una ejecucion mas
                            }                                                         
                        } catch (CloneNotSupportedException ex) {                            
                        }
                    }else {                                                
                        if(Logic.TIMECPU == Logic.LIMIT-1) {
                            if(tmp.getBurstEnd() != 0 || Logic.dataqueue2.size() >= 1) {
                                Logic.LIMIT++; // En el caso especial de desfase
                                DataController.xAxis.setUpperBound(DataController.xAxis.getUpperBound()+1); // Agrego una ejecucion mas
                            }
                        }                                                                                                                        
                    }                                                                                                   
                    freeProcess(Logic.dataqueue2, false); // Libero los procesos que ya hicieron E/S                
                    return tmp;
                }
                freeProcess(Logic.dataqueue2, false); // Libero los procesos que ya hicieron E/S                
                break;
            }
            
            case " SJN": { // Algoritmo SJN                
                MyProcess tmp = algorithmSJN(Logic.dataqueue2); // Proceso a ejecutar
                if(tmp != null) { // Si hay alguno disponible                    
                    if(tmp.getBurstStart() != 0) { // Primeras rafagas
                        tmp.setBurstStart(tmp.getBurstStart()-1);  // Quito una rafaga
                        tmp.setBurstExecute(tmp.getBurstExecute()+1); // Aumento las rafagas ejecutadas  
                    }else { // Cambio de rafagas                                
                        if(tmp.getBurstEnd() != 0) { // Si no ha acabo sus rafagas
                            tmp.setBurstEnd(tmp.getBurstEnd()-1); // Quito una rafaga
                            tmp.setBurstExecute(tmp.getBurstExecute()+1); // Aumento las Rafagas ejecutadas 
                            if(tmp.getBurstEnd() == 0) tmp.setExecute(true); // Verifico si ya acabo                                                         
                        }                         
                    }                    
                    if(Logic.TIMECPU == Logic.LIMIT-1) { // Si es la ultima tiempo de CPU
                        if(tmp.getBurstEnd() != 0 || Logic.dataqueue2.size() >= 1) { // Si aun queda alguien por ejecutar
                            Logic.LIMIT++; // En el caso especial de desfase
                            DataController.xAxis.setUpperBound(DataController.xAxis.getUpperBound()+1); // Agrego una ejecucion mas en la grafica
                        }
                    }                      
                }else { // En caso de que no haya nadie disponble en el actual Tiempo de CPU
                    if(!Logic.dataqueue2.isEmpty()) { // Si hay alguien por ejecutar luego
                        tmp = Logic.dataqueue2.get(0);
                        if(Logic.TIMECPU == Logic.LIMIT-1) { // Si es la ultima ejecucio
                            if(tmp.getBurstEnd() != 0 || Logic.dataqueue2.size() >= 1) { // Si aun queda alguien por ejecutar
                                Logic.LIMIT++; // En el caso especial de desfase
                                DataController.xAxis.setUpperBound(DataController.xAxis.getUpperBound()+1); // Agrego una ejecucion mas
                            }
                        }  
                    }
                    
                }                
                freeProcess(Logic.dataqueue2, false); // Libero los procesos que ya hicieron E/S    
                return tmp;                
            }
            
            case " HRN": {                
                MyProcess tmp = algorithmHRN(Logic.dataqueue2); // Proceso a ejecutar                                
                if(tmp != null) {
                    if(tmp.getBurstStart() != 0) { // Primeras rafagas
                        tmp.setBurstStart(tmp.getBurstStart()-1);  // Quito una rafaga
                        tmp.setBurstExecute(tmp.getBurstExecute()+1); // Aumento las rafagas ejecutadas  
                    }else { // Cambio de rafagas                                
                        if(tmp.getBurstEnd() != 0) { // Si no ha acabo sus rafagas
                            tmp.setBurstEnd(tmp.getBurstEnd()-1); // Quito una rafaga
                            tmp.setBurstExecute(tmp.getBurstExecute()+1); // Aumento las Rafagas ejecutadas 
                            if(tmp.getBurstEnd() == 0) tmp.setExecute(true); // Verifico si ya acabo                                                         
                        }                         
                    }
                                        
                    if(Logic.TIMECPU == Logic.LIMIT-1) { // Si es la ultima tiempo de CPU
                        if(tmp.getBurstEnd() != 0 || Logic.dataqueue2.size() >= 1) { // Si aun queda alguien por ejecutar
                            Logic.LIMIT++; // En el caso especial de desfase
                            DataController.xAxis.setUpperBound(DataController.xAxis.getUpperBound()+1); // Agrego una ejecucion mas en la grafica
                        }
                    } 
                }else {
                    if(!Logic.dataqueue2.isEmpty()) { // Si hay alguien por ejecutar luego
                        tmp = Logic.dataqueue2.get(0);
                        if(Logic.TIMECPU == Logic.LIMIT-1) { // Si es la ultima ejecucio
                            if(tmp.getBurstEnd() != 0 || Logic.dataqueue2.size() >= 1) { // Si aun queda alguien por ejecutar
                                Logic.LIMIT++; // En el caso especial de desfase
                                DataController.xAxis.setUpperBound(DataController.xAxis.getUpperBound()+1); // Agrego una ejecucion mas
                            }
                        }  
                    }
                }                
                freeProcess(Logic.dataqueue2, false); // Libero los procesos que ya hicieron E/S    
                return tmp;                
            }
            default: {
                Notification notification = new Notification("No coincide ningun algoritmo", 
                        "Por favor verifica que elegiste un algoritmo valido");    
            }
        }
        return null;
    }  

    public static MyProcess algorithmHRN(ArrayList<MyProcess> candidates) {
        ArrayList<MyProcess> filter = (ArrayList<MyProcess>) candidates.clone(); // Clono los datos       
        for(MyProcess item : filter) { 
            if(item.isFlagQueue() || item.isExecute()) { // Si hay algun proceso que ya acabo o esta en otra cola
                candidates.remove(item); // Elimino el proceso de los candidatos a ejecutar
            }
        }        
        if(!candidates.isEmpty()) {
            MyProcess max = candidates.get(0); // Tomo un aspirante como valor inicial
            for(int i=0; i<candidates.size(); i++) { // Recorro los candidatos
                MyProcess current = candidates.get(i); // Tomo el actual                
                MyProcess currentOrigin = new MyProcess(); // Genero un auxiliar para guardar los datos originales de las rafagas del proceso actual
                MyProcess maxOrigin = new MyProcess(); // Genero un auxiliar para guardar los datos originales de las rafagas del proceso para HRN
                if(!current.isExecute() && !current.isBusy()) { // Si no ha terminado de ejecutar y no esta ocupado
                    currentOrigin = findProcess(Logic.dataOrigin, current); // Tomo los datos originales del proceso actual
                    maxOrigin = findProcess(Logic.dataOrigin, max); // Tomo los datos originales del proecso maximo HRN
                }else continue; // Si esta ocupado
                
                if(max.isExecute() || max.isBusy()) max = current; // Si ya ejecutado todo el proceso con maximo de HRN o esta ocupado
                else {
                    if(!Logic.camino2.isBlank()) { // Si ya se ejecuto alguien en la cola 2
                        String aux = ""+Logic.camino2.charAt(Logic.camino2.length()-1); 
                        if(current.getId().equals(aux) && // Si el ultimo que se ejecuto no ha terminado toda su ejecucion
                            current.getBurstExecute() > 0) return current; // Devuelvo el actual por ser HRN no apropiativo
                    }                                        
                    float work1 = -1, work2 = -1; // Variable para guardar las rafagas totales para la comparacion 
                    if(current.getBurstStart() != 0) { // Si esta ejecutando las rafagas iniciales
                        work1 = (float)((Logic.TIMECPU - current.getLastExecucion()) +
                                currentOrigin.getBurstStart()) / currentOrigin.getBurstStart();
                    }else { // Si esta ejecutando las rafagas finales
                        work1 = (float)((Logic.TIMECPU - current.getLastExecucion()) +
                                currentOrigin.getBurstEnd()) / currentOrigin.getBurstEnd();                    
                    }
                    if(max.getBurstStart() != 0) { // Si esta ejecutando las rafagas finales
                        work2 = (float)((Logic.TIMECPU - max.getLastExecucion()) +
                                maxOrigin.getBurstStart()) / maxOrigin.getBurstStart();                    
                    }else if(!max.isExecute()){ // Si esta ejecutando las rafagas finales y no ha terminado     
                        work2 = (float)((Logic.TIMECPU - max.getLastExecucion()) +
                                maxOrigin.getBurstEnd()) / maxOrigin.getBurstEnd();
                    }

                    if(work1 != -1 && work2 != -1 && work2 < work1 ) { // Comparo para saber si el proceso actual tiene un HRN mayor que el max
                        max = current;                    
                    }                                        
                }                
            }          
            if(max.isExecute() || max.isBusy()) return null; // En caso tal que el que tenga el SJN este ocupado o ya halla acabado de ejecutar
            else return max; // Sino devuelve el proceso que se debe ejecutar            
        }else return null;                
    }
    
    public static MyProcess algorithmSJN(ArrayList<MyProcess> candidates) {
        ArrayList<MyProcess> filter = (ArrayList<MyProcess>) candidates.clone(); // Clono los datos
        for(MyProcess item : filter) { 
            if(item.isFlagQueue() || item.isExecute()) { // Si hay algun proceso que ya acabo o esta en otra cola
                candidates.remove(item); // Elimino el proceso de los candidatos a ejecutar
            }
        }
        if(!candidates.isEmpty()) { // Si hay candidatos
            MyProcess min = filter.get(0); // Genero un dato iniciar del trabajo mas corto
            for(int i=0; i<candidates.size(); i++) { // Recorro los candidatos
                MyProcess current = candidates.get(i); // Tomo el proceso actual                                
                MyProcess currentOrigin = new MyProcess(); // Genero un auxiliar para guardar los datos originales de las rafagas del proceso actual
                MyProcess minOrigin = new MyProcess(); // Genero un auxiliar para guardar los datos originales de las rafagas del proceso con el trabajo mas corto
                if(!current.isExecute() && !current.isBusy()) { // Si no ha terminado de ejecutar y no esta ocupado
                    currentOrigin = findProcess(Logic.dataOrigin, current); // Tomo los datos originales del actual proceso
                    minOrigin = findProcess(Logic.dataOrigin, min); // Tomo los datos originales del proceso con trabajo mas corto
                }else continue; // Si el proceso actual esta ocupado o ya termino de ejecutar todo
                                                
                if(min.isExecute() || min.isBusy()) min = current; // Si ya ejecutado todo el proceso con trabajo mas corto
                else {
                    int work1 = -1, work2 = -1; // Variable para guardar las rafagas totales para la comparacion 
                    if(current.getBurstStart() != 0) { // Si esta ejecutando las rafagas iniciales
                        work1 = currentOrigin.getBurstStart();                    
                    }else { // Si esta ejecutando las rafagas finales
                        work1 = currentOrigin.getBurstEnd();                        
                    }        
                    
                    if(!Logic.camino2.isBlank()) { // Si ya se ejecuto alguien en la cola 2
                        String aux = ""+Logic.camino2.charAt(Logic.camino2.length()-1); 
                        if(current.getId().equals(aux) && // Si el ultimo que se ejecuto no ha terminado toda su ejecucion
                            current.getBurstExecute() > 0) return current; // Devuelvo el actual por ser SJN no apropiativo
                    }
                    
                    if(min.getBurstStart() != 0) { // Si esta ejecutando las rafagas finales
                        work2 = minOrigin.getBurstStart();
                    }else if(!min.isExecute()){ // Si esta ejecutando las rafagas finales y no ha terminado     
                        work2 = minOrigin.getBurstEnd();
                    }
                                        
                    if(work1 != -1 && work2 != -1 && work2 > work1 ) { // Comparo para saber si el proceso actual tiene un trabajo mas corto
                        min = current;                    
                    }                                        
                }
            }   
            if(min.isExecute() || min.isBusy()) return null; // En caso tal que el que tenga el SJN este ocupado o ya halla acabado de ejecutar
            else return min; // Sino devuelve el proceso que se debe ejecutar
            
        }else return null; // Si no hay candidatos
    }
    
    public static MyProcess algorithmFCFS(ArrayList<MyProcess> candidate) {
        ArrayList<MyProcess> filter = (ArrayList<MyProcess>) candidate.clone(); // Clono los datos
        for(MyProcess item : filter) {            
            if(item.isFlagQueue() || item.isExecute()) { // Si esta en otra cola o ya termino de ejecutar
                candidate.remove(item); // Elimino en el caso que cumpla la condicion anterior
            }
        }
        if(candidate.size() == 1 && candidate.get(0).isBusy()) return null;  // Si solo hay un candidato y esta ocupado
                
        return (candidate.isEmpty()) ? null : candidate.get(0); // Si hay candidatos retorno el primero sino null
    }
   
    public static MyProcess orderBypriority(ArrayList<MyProcess> candidate) {
        int index = -1;
        if(candidate.size() == 1) { // En caso de haber solo un candidato            
            return candidate.get(0);
        }else {            
            index = 0;      
            ArrayList<MyProcess> filter = (ArrayList<MyProcess>) candidate.clone(); // Clono los datos
            for(MyProcess item : filter) {             
                if(item.isExecute()) {
                    candidate.remove(item); // Elimino en caso de que algun candidato que ya haya acado todo
                }
            }
            filter = (ArrayList<MyProcess>) candidate.clone(); // Vuelvo a clonar para actualizar datos
            for(MyProcess item : candidate) {             
                if(!item.isFlagQueue() || item.isBusy()) {
                    filter.remove(item); // Elimino en caso de que algun candidato este ocupado o este en la otra cola
                }
            }             
            if(!filter.isEmpty()) { // Si hay candidatos
                for(int i=0; i<filter.size()-1; i++){
                    if(filter.get(i+1).getPriority() > 
                       filter.get(i).getPriority()){ // Tomo el de mayor prioridad                  
                       index = i+1;
                    }
                }                
                return filter.get(index); // Retorno el proceso con mayor prioridad
            }else return null;  // Si no hay procesos en el momento devuelvo null   
        }
    }
    
    public static void freeProcess(ArrayList<MyProcess> myProcess, boolean freeAll) {         
        for(MyProcess item : myProcess) {
            // Liberar si ya termino E/S
            if(freeAll) item.setBusy(false); // En caso de querer liberar a todos los procesos
            else if(item.isBusy()) { // Si el proceso esta ocupado                
                item.setCounterBusy(item.getCounterBusy()-1); // Descuento de los procesos ocupados
                if(item.getCounterBusy() == 0) { // Si ya acabo con E/S lo libero para ser candidato                    
                    item.setBusy(false); // Liberarlo
                }
            }          
        }        
    }
    
    public static int totalBurst(ArrayList<MyProcess> myProcess) {
        int limit = 0;
        for(MyProcess item : myProcess) {
            limit += (item.getBurstStart() + item.getBurstEnd()); // Calculo el total de rafagas de ejecucion de todos los procesos
        }
        return limit;
    }
    
    public static ArrayList<MyProcess> joinData(ArrayList<MyProcess> source, HashMap<String, Integer> resul) {
        for(MyProcess item : source) { // Recorro la lista final de procesos
            item.setTimeFinal(resul.get(item.getId())); // Doy el tiempo final de cpu
            item.setTimeService(resul.get(item.getId()) - item.getArrival()); // Tiempo de servicio
            item.setTimeInstant(item.getTimeService() - 
                    (item.getBurstStart()+item.getBurstES()+item.getBurstEnd())); //             
            DecimalFormat aux = new DecimalFormat("####.00"); // Objeto para redondear y aproximar la division del indice
            DecimalFormatSymbols symbol = new DecimalFormatSymbols(); // Objeto para evitar problemas con los Simbolos de ',' y '.' segun el SO
            symbol.setDecimalSeparator('.'); // Indicar que la separacion es por punto decimal
            aux.setDecimalFormatSymbols(symbol); // Agregarle la configuracion al objeto de DecimalFormat
            float index = (float) (item.getBurstStart()+item.getBurstEnd()) / item.getTimeService(); // Indice del proceso              
            index = Float.parseFloat(aux.format(index)); // Darle formato al decimal            
            item.setIndexService(index); // Lo almaceno el indice
        }
        return source;
    }
    
    public static HashMap<String, Integer> createHashMap(ArrayList<MyProcess> list) {
        HashMap<String, Integer> result = new HashMap<>();
        for(MyProcess item : list) {
            result.put(item.getId(), Integer.MIN_VALUE); // Creo un HashMap con base a los procesos con un valor inicial
        }
        return result;
    }
    
    public static MyProcess findProcess(ArrayList<MyProcess> list, MyProcess object) {
        MyProcess source = new MyProcess();
        for(MyProcess item : list) {
            if(item.getId().equalsIgnoreCase(object.getId())) { // Busco un proceso especifico dentro de los datos originales
               source = item;
           }
        }
        return source;
    }
    
    public static void viewData(ArrayList<MyProcess> list) {        
        for(MyProcess item : list) {            
        }        
    }
    
    public static ArrayList<MyProcess> cloneData(ArrayList<MyProcess> list) {
        ArrayList<MyProcess> copy = new ArrayList<>();
        for(int i=0; i<list.size(); i++) {
            try {            
                copy.add((MyProcess) list.get(i).clone());
            } catch (CloneNotSupportedException ex) {
                System.out.println("Error al clonar");
            }
        }
        return copy;
    }
    
    
    public static ArrayList<MyProcess> dataRandom(int amount) {
        ArrayList<MyProcess> process = new ArrayList<>();
        Random random = new Random(System.nanoTime());
        char id = 65;        
        int min = 1, max = 7;
        amount = random.nextInt(9) + 2; // Aleatorio entre 2 - 10
        for(int i=0; i<amount; i++) { // Generar procesos aleatorios
            int arrival, burstStart, burstEnd, burstES, priority;            
            arrival = random.nextInt(max); // Aleatorio entre 0 - 6 para T. llegada
            burstStart = random.nextInt(max - min) + min; // Aleatorio entre 1 - 6 para Rafagas iniciales
            burstEnd = random.nextInt(max - min) + min; // Aleatorio entre 1 - 6 para Rafagas finales
            burstES = random.nextInt(max - min) + min; // Aleatorio entre 1 - 6 para Rafagas de E/S
            priority = random.nextInt(max - min) + min; // Aleatorio entre 1 - 6 para prioridad
            process.add(new MyProcess(Character.toString(id), arrival, burstStart, burstEnd, burstES, priority)); // Agrego el proceso con los datos generados
            id++; // Aumento para construir el id;
        }        
        viewData(process);
        return process;
    }
}
