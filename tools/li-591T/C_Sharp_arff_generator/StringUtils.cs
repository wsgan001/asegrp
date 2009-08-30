using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CloneFinder
{
    class StringUtils
    {
        public int LongestCommonSubstring(string str1, string str2)
        {
            if (String.IsNullOrEmpty(str1) || String.IsNullOrEmpty(str2))
                return 0;

            int[,] num = new int[str1.Length, str2.Length];
            int maxlen = 0;

            for (int i = 0; i < str1.Length; i++)
            {
                for (int j = 0; j < str2.Length; j++)
                {
                    if (str1[i] != str2[j])
                        num[i, j] = 0;
                    else
                    {
                        if ((i == 0) || (j == 0))
                            num[i, j] = 1;
                        else
                            num[i, j] = 1 + num[i - 1, j - 1];

                        if (num[i, j] > maxlen)
                        {
                            maxlen = num[i, j];
                        }
                    }
                }
            }
            return maxlen;
        }

        public int FastDiffLCS(Object[] obja, Object[] objb)
        {
            int len1 = obja.Length, len2 = objb.Length;

            Boolean use1 = len1 < len2;
            int n = len1 + len2;
            int m = use1 ? len1 : len2;
            n = n - m;
            Object[] a = (use1 ? obja : objb);
            Object[] b = (use1 ? objb : obja);
            int m_start = 1;
            int m_end = m;
            int n_start = 1;
            int n_end = n;
            // trim off the matching items at the beginning
            while (m_start < m_end && n_start < n_end
                    && a[m_start - 1].Equals(b[n_start - 1]))
            {
                m_start++;
                n_start++;
            }
            // trim off the matching items at the end
            while (m_start < m_end && n_start < n_end
                    && a[m_end - 1].Equals(b[n_end - 1]))
            {
                m_end--;
                n_end--;
            }
            int[][] arr2 = new int[2][];
            for (int i = 0; i < arr2.Length; i++)
            {
                arr2[i] = new int[m_end - m_start + 2];
            }
            for (int i = 0; i <= m_end - m_start + 1; i++)
                for (int j = 0; j < 2; j++)
                    arr2[j][i] = 0;
            // HashMap<int[],String> matrix=new HashMap<int[],String>();

            for (int j = 1; j <= n_end - n_start + 1; j++)
                for (int i = 1; i <= m_end - m_start + 1; i++)
                {
                    if (a[i - 1].Equals(b[j - 1]))
                        arr2[j % 2][i] = arr2[(j - 1) % 2][i - 1] + 1;
                    else
                        arr2[j % 2][i] = Math.Max(arr2[j % 2][i - 1],
                                arr2[1 - j % 2][i]);
                }
            return arr2[(n_end - n_start + 1) % 2][m_end - m_start + 1] + a.Length - m_end + m_start - 1;

        }

        public static int FastLCS(Object[] obja, Object[] objb)
        {
            int len1 = obja.Length, len2 = objb.Length;

            Boolean use1 = len1 < len2;
            int n = len1 + len2;
            int m = use1 ? len1 : len2;
            n = n - m;
            Object[] a = (use1 ? obja : objb);
            Object[] b = (use1 ? objb : obja);
            int m_start = 0;
            int m_end = m;
            int n_start = 0;
            int n_end = n;
            // trim off the matching items at the beginning
            while (m_start < m_end && n_start < n_end
                    && a[m_start].Equals(b[n_start]))
            {
                m_start++;
                n_start++;
            }
            // trim off the matching items at the end
            while (m_start < m_end && n_start < n_end
                    && a[m_end - 1].Equals(b[n_end - 1]))
            {
                m_end--;
                n_end--;
            }

            int[][] arr2 = new int[2][];
            /*for (int i = 0; i < arr2.Length; i++)
            {
                arr2[i] = new int[m_end - m_start + 1];
            }*/
            arr2[0] = new int[n_end - n_start + 1];
            arr2[1] = new int[m_end - m_start + 1];

            for (int i = 0; i <= n_end - n_start; i++)
                arr2[0][i] = 0;
            for (int i = 0; i <= m_end - m_start; i++)
                arr2[1][i] = 0;

            // HashMap<int[],String> matrix=new HashMap<int[],String>();

            for (int j = 1; j < n_end - n_start + 1; j++)
                for (int i = 1; i < m_end - m_start + 1; i++)
                {
                    if (a[i + m_start - 1].Equals(b[j + n_start - 1]))
                        arr2[j % 2][i] = arr2[(j - 1) % 2][i - 1] + 1;
                    else
                        arr2[j % 2][i] = Math.Max(arr2[j % 2][i - 1],
                                arr2[1 - j % 2][i]);
                }
            return arr2[(n_end - n_start + 1) % 2][m_end - m_start] + a.Length - m_end + m_start;

        }


        public static void GetDiffWords(Object[] obja, Object[] objb)
        {
            int len1 = obja.Length, len2 = objb.Length;

            Boolean use1 = len1 < len2;
            int n = len1 + len2;
            int m = use1 ? len1 : len2;
            n = n - m;
            Object[] a = (use1 ? obja : objb);
            Object[] b = (use1 ? objb : obja);
            int m_start = 0;
            int m_end = m;
            int n_start = 0;
            int n_end = n;
            // trim off the matching items at the beginning
            while (m_start < m_end && n_start < n_end
                    && a[m_start].Equals(b[n_start]))
            {
                m_start++;
                n_start++;
            }
            // trim off the matching items at the end
            while (m_start < m_end && n_start < n_end
                    && a[m_end - 1].Equals(b[n_end - 1]))
            {
                m_end--;
                n_end--;
            }

            int[][] arr2 = new int[2][];
            /*for (int i = 0; i < arr2.Length; i++)
            {
                arr2[i] = new int[m_end - m_start + 1];
            }*/
            arr2[0] = new int[n_end - n_start + 1];
            arr2[1] = new int[m_end - m_start + 1];

            for (int i = 0; i <= n_end - n_start; i++)
                arr2[0][i] = 0;
            for (int i = 0; i <= m_end - m_start; i++)
                arr2[1][i] = 0;

            // HashMap<int[],String> matrix=new HashMap<int[],String>();

            for (int j = 1; j < n_end - n_start + 1; j++)
                for (int i = 1; i < m_end - m_start + 1; i++)
                {
                    if (a[i + m_start - 1].Equals(b[j + n_start - 1]))
                        arr2[j % 2][i] = arr2[(j - 1) % 2][i - 1] + 1;
                    else
                        arr2[j % 2][i] = Math.Max(arr2[j % 2][i - 1],
                                arr2[1 - j % 2][i]);
                }

            int lll =  arr2[(n_end - n_start + 1) % 2][m_end - m_start] + a.Length - m_end + m_start;

         }

       
        private static void PrintDiff(int[][] arr2, Object[] obja, Object[] objb, int i, int j)
        {
            if (i > 0 && j > 0 && obja[i] == objb[j]) 
            {
                PrintDiff(arr2, obja, objb, i - 1, j - 1);
                Console.Out.Write("  " + obja[i]);
            }
            else if (j > 0 && (i == 0 || arr2[i][j-1] >= arr2[i-1][j])){
                PrintDiff(arr2, obja, objb, i, j - 1);
                Console.Out.Write("+ " + objb[j]);
            }
            else if (i > 0 && (j == 0 || arr2[i][j-1] < arr2[i-1][j])){
                PrintDiff(arr2, obja, objb, i - 1, j);
                Console.Out.Write("- " + obja[i]);
            }

        }

    }
}
