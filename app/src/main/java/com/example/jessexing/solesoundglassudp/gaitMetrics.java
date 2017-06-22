package com.example.jessexing.solesoundglassudp;

/**
 * Created by jessexing on 7/12/16.
 */
public class gaitMetrics {
    private double l_hs_prev;
    private double l_to;
    private double l_hs_curr;
    private double r_hs_prev;
    private double r_to;
    private double r_hs_curr;


    public gaitMetrics() {
        this.l_hs_curr = 0;
        this.l_to = 0;
        this.l_hs_prev = 0;

        this.r_hs_prev = 0;
        this.r_to = 0;
        this.r_hs_curr = 0;
    }

    public void setParameters(long l_hs_prev, long l_to, long l_hs_curr, long r_hs_prev, long r_to, long r_hs_curr) {
        this.l_hs_curr = (double) l_hs_curr;
        this.l_to = (double) l_to;
        this.l_hs_prev = (double) l_hs_prev;

        this.r_hs_prev = (double) r_hs_prev;
        this.r_to = (double) r_to;
        this.r_hs_curr = (double) r_hs_curr;
    }

    public double getCadence() {
        double avg = ((l_hs_curr-l_hs_prev) + (r_hs_curr-r_hs_prev))/(2*1000.0);
        if(avg ==0) {
            return 0;
        }
        return (1/avg)*120;
    }

    public double getSwingStance_L() {
        if(l_to-l_hs_prev ==0) {
            return 0;
        }
        return (l_hs_curr-l_to)/(l_to-l_hs_prev);
    }

    public double getSwingStance_R() {
        if(r_to-r_hs_prev ==0) {
            return 0;
        }
        return (r_hs_curr-r_to)/(r_to-r_hs_prev);
    }

    public double getSymmetry() {
        double v_l = getSwingStance_L();
        double v_r = getSwingStance_R();
        return (v_l-v_r)/(.5*(v_l+v_r));
    }
}
