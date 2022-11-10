package com.example.swishbirthday;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Birthday {
    private String nombre;
    private Date fecha;
    private String hora;

    public Birthday(String nombre, Date fecha, String hora) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
    }

    public String getNombre() {
        return nombre;
    }

    private void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFecha() {
        return fecha;
    }

    private void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    private void setHora(String hora) {
        this.hora = hora;
    }

    @Override
    public String toString() {
        return  nombre + "\n" + new SimpleDateFormat("MMM dd, yyy").format(fecha) + "   " + (hora == null ? "          " : hora) + "                                       " + (TimeUnit.MILLISECONDS.toDays(new Date().getTime() - fecha.getTime())/365 +1);
    }
}
