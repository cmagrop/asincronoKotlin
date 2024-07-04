package com.example.asincrono

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlin.random.Random

fun main()
{
    //globalScope()
    //courutinesVsThreads()
    //cAsync()
    //job()
    //basicFlows() //flow es un tipo de corrutina
    //nestedCourutines()
    excepcions()

}

//completar
fun excepcions() {
    val exceptionHandler = CoroutineExceptionHandler{
        coroutineContext, throwable ->
        println("Notificar error $throwable in $coroutineContext")

        if(throwable is ArithmeticException)
            println("Reintentar")
    }

    runBlocking {

        launch {

            try {
                delay(100)
                throw  Exception()
            }
            catch (e:Exception)
            {
                e.printStackTrace()
            }

        }

    }
}

fun nestedCourutines() {
    runBlocking {
        println("Anidar")
        val job = launch {
            mensajeInicial()

            launch { //subproceso
                mensajeInicial()
                //delay(4000)
                println("subproceso 1")
                mensajeFinal()

            }

            val subJob = launch(Dispatchers.IO) //subproceso
            {
                mensajeInicial()
                launch(newSingleThreadContext("subproceso Dispatcher IO")) //subproceso
                {
                    mensajeInicial()
                    println("subproceso de IO Dispatcher")
                    mensajeFinal()
                }

                delay(3000)
                println("Informacion enviada al servidor")
                mensajeFinal()

            }

            delay(2000)
            subJob.cancel() //cancelar la tarea en ejecucion
            println("Subida al servidor cancelada")


        }

        job.cancel() //suspendiendo el proceso principal

    }
}

fun basicFlows() {
    println("Flows básicos")
    runBlocking {

        launch {
            getDataByFlow().collect{
                println(it)
            }

        }

        launch {
            (1..5).forEach {

                delay(1000)
                println("nuevo flujo")

            }
        }
    }
}

fun getDataByFlow():Flow<Float> {

    return flow {
        (1..5).forEach {

            //println("Procesando datos")
            delay(5000)
            emit(it+Random.nextFloat())

        }


    }
}

//permite tener un mayor control sobre una corrutina
fun job() {
    runBlocking {
        val job = launch {

            mensajeInicial()
            delay(500)
            println("job...")
            mensajeFinal()

        }

        println("Job: $job")

        println("isActive: ${job.isActive}")
        println("isCancelled: ${job.isCancelled}")
        println("isCompleted: ${job.isCompleted}")

        delay(1000)
        println("Tarea cancelada o interrumpida")
        job.cancel()

        println("isActive: ${job.isActive}")
        println("isCancelled: ${job.isCancelled}")
        println("isCompleted: ${job.isCompleted}")
    }
}

fun cAsync() {
    runBlocking {

        val resultado = async {

            mensajeInicial()
            delay(randomTime())
            println("async...")
            mensajeFinal()

        }

        println("Resultado: ${resultado.await()}")


    }
}

//GlobalScope.launch->
fun globalScope() {

    GlobalScope.launch { 
        mensajeInicial()
        delay(randomTime()) //tiempo aleatorio
        println("Mi corrutina")
        mensajeFinal()

    }

}

fun mensajeFinal() {
    println("Corrutina - ${Thread.currentThread().name}")
}

fun randomTime(): Long = Random.nextLong(500,2_000)

fun mensajeInicial() {
    println("Comenzando la corrutina - ${Thread.currentThread().name}")
}

fun courutinesVsThreads() {

   //implementar Thread
    /*
    (1..500).forEach {
        thread {
            Thread.sleep(5000)
            print("*" )
        }
    }
    */


    //corrutines

    runBlocking {

        (1..500).forEach {

            launch {
                delay(5000)
                print("*")
            }

        }

    }


}