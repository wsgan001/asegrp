using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Pex.Framework;
using System.IO;

namespace CombinatorialTest
{
    [PexClass]
    public partial class TCAS
    {

        /*  -*- Last-Edit:  Fri Jan 29 11:13:27 1993 by Tarak S. Goradia; -*- */
        /* $Log: tcas.c,v $
         * Revision 1.2  1993/03/12  19:29:50  foster
         * Correct logic bug which didn't allow output of 2 - hf
         * */

        //#include <stdio.h>

        static int OLEV = 600;	/* in feets/minute */
        static int MAXALTDIFF = 600;		/* max altitude difference in feet */
        static int MINSEP = 300;          /* min separation in feet */
        static int NOZCROSS = 100;		/* in feet */
        /* variables */

        //typedef int bool;

        static int Cur_Vertical_Sep;
        static bool High_Confidence;
        static bool Two_of_Three_Reports_Valid;

        static int Own_Tracked_Alt;
        static int Own_Tracked_Alt_Rate;
        static int Other_Tracked_Alt;

        static int Alt_Layer_Value;		/* 0, 1, 2, 3 */
        static int[] Positive_RA_Alt_Thresh = new int[4];

        static int Up_Separation;
        static int Down_Separation;

        /* state variables */
        static int Other_RAC;			/* NO_INTENT, DO_NOT_CLIMB, DO_NOT_DESCEND */
        static int NO_INTENT = 0;
        static int DO_NOT_CLIMB = 1;
        static int DO_NOT_DESCEND = 2;

        static int Other_Capability;		/* TCAS_TA, OTHER */
        static int TCAS_TA = 1;
        static int OTHER = 2;

        static bool Climb_Inhibit;		/* true/false */

        static int UNRESOLVED = 0;
        static int UPWARD_RA = 1;
        static int DOWNWARD_RA = 2;

        private static void initialize()
        {
            Positive_RA_Alt_Thresh[0] = 400;
            Positive_RA_Alt_Thresh[1] = 500;
            Positive_RA_Alt_Thresh[2] = 640;
            Positive_RA_Alt_Thresh[3] = 740;
        }

        private static int ALIM()
        {
            return Positive_RA_Alt_Thresh[Alt_Layer_Value];
        }

        private static int Inhibit_Biased_Climb()
        {
            return (Climb_Inhibit ? Up_Separation + NOZCROSS : Up_Separation);
        }

        private static bool Non_Crossing_Biased_Climb()
        {
            bool upward_preferred;
            int upward_crossing_situation;
            bool result=false;

            upward_preferred = Inhibit_Biased_Climb() > Down_Separation;
            if (upward_preferred)
            {
                //PexAssume.IsTrue(Down_Separation == ALIM() && result);
                result = !(Own_Below_Threat()) || ((Own_Below_Threat()) && (!(Down_Separation > ALIM()))); /* opertor mutation */
            }
            else
            {
                result = Own_Above_Threat() && (Cur_Vertical_Sep >= MINSEP) && (Up_Separation >= ALIM());
            }
            return result;
        }

        private static bool Non_Crossing_Biased_Descend()
        {
            bool upward_preferred;
            int upward_crossing_situation;
            bool result;

            upward_preferred = Inhibit_Biased_Climb() > Down_Separation;
            if (upward_preferred)
            {
                result = Own_Below_Threat() && (Cur_Vertical_Sep >= MINSEP) && (Down_Separation >= ALIM());
            }
            else
            {
                result = !(Own_Above_Threat()) || ((Own_Above_Threat()) && (Up_Separation >= ALIM()));
            }
            return result;
        }

        private static bool Own_Below_Threat()
        {
            return (Own_Tracked_Alt < Other_Tracked_Alt);
        }

        private static bool Own_Above_Threat()
        {
            return (Other_Tracked_Alt < Own_Tracked_Alt);
        }

        private static int alt_sep_test()
        {
            bool enabled, tcas_equipped, intent_not_known;
            bool need_upward_RA, need_downward_RA;
            int alt_sep;

            enabled = High_Confidence && (Own_Tracked_Alt_Rate <= OLEV) && (Cur_Vertical_Sep > MAXALTDIFF);
            tcas_equipped = Other_Capability == TCAS_TA;
            intent_not_known = Two_of_Three_Reports_Valid && Other_RAC == NO_INTENT;

            alt_sep = UNRESOLVED;

            if (enabled && ((tcas_equipped && intent_not_known) || !tcas_equipped))
            {
                need_upward_RA = Non_Crossing_Biased_Climb() && Own_Below_Threat();
                need_downward_RA = Non_Crossing_Biased_Descend() && Own_Above_Threat();
                if (need_upward_RA && need_downward_RA)
                    /* unreachable: requires Own_Below_Threat and Own_Above_Threat
                       to both be true - that requires Own_Tracked_Alt < Other_Tracked_Alt
                       and Other_Tracked_Alt < Own_Tracked_Alt, which isn't possible */
                    alt_sep = UNRESOLVED;
                else if (need_upward_RA)
                {
                    alt_sep = UPWARD_RA;                    
                }
                else if (need_downward_RA)
                    alt_sep = DOWNWARD_RA;
                else
                    alt_sep = UNRESOLVED;
            }

            return alt_sep;
        }

        [PexMethod(MaxRuns = 20, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue)]
        public void TCASMain(int[] argv)
        {
            if (argv.Length < 13)
            {
                return;
            }
            initialize();
            Cur_Vertical_Sep = argv[1];
            High_Confidence = (argv[2] == 1);
            Two_of_Three_Reports_Valid = (argv[3] == 1);
            Own_Tracked_Alt = argv[4];
            Own_Tracked_Alt_Rate = argv[5];
            Other_Tracked_Alt = argv[6];
            Alt_Layer_Value = argv[7];
            Up_Separation = argv[8];
            Down_Separation = argv[9];
            Other_RAC = argv[10];
            Other_Capability = argv[11];
            Climb_Inhibit = (argv[12] == 1);

            int result = alt_sep_test();
            PexStore.Value("PC", PexSymbolicValue.GetPathConditionString());
            PexStore.Value("Result", result);     
            
            
        }

    }
}
