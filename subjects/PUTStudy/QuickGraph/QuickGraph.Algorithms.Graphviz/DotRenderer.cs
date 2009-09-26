/// QuickGraph Library 
/// 
/// Copyright (c) 2003 Jonathan de Halleux
///
/// This software is provided 'as-is', without any express or implied warranty. 
/// 
/// In no event will the authors be held liable for any damages arising from 
/// the use of this software.
/// Permission is granted to anyone to use this software for any purpose, 
/// including commercial applications, and to alter it and redistribute it 
/// freely, subject to the following restrictions:
///
///		1. The origin of this software must not be misrepresented; 
///		you must not claim that you wrote the original software. 
///		If you use this software in a product, an acknowledgment in the product 
///		documentation would be appreciated but is not required.
///
///		2. Altered source versions must be plainly marked as such, and must 
///		not be misrepresented as being the original software.
///
///		3. This notice may not be removed or altered from any source 
///		distribution.
///		
///		QuickGraph Library HomePage: http://www.dotnetwiki.org
///		Author: Jonathan de Halleux



namespace QuickGraph.Algorithms.Graphviz
{
	using System;
	using System.IO;
	using System.Text;

	/// <summary>
	/// A dot commandline wrapper
	/// </summary>
	public class DotRenderer
	{
		private String m_Prefix;
		private String m_Path;
		private GraphvizImageType m_ImageType;

		/// <summary>
		/// Default constructor
		/// </summary>
		public DotRenderer()
		{
			Prefix="dot";
			Path="";
			m_ImageType = GraphvizImageType.Png;
		}

		/// <summary>
		/// Detailled constructor
		/// </summary>
		/// <param name="prefix"></param>
		/// <param name="path"></param>
		/// <param name="imageType"></param>
		public DotRenderer(String prefix,String path,GraphvizImageType imageType)
		{
			Prefix = prefix;
			Path = path;
			m_ImageType = imageType;
		}

		/// <summary>
		/// String to create the file name
		/// </summary>
		public String Prefix
		{
			get
			{
				return m_Prefix;
			}
			set
			{
				m_Prefix = value.TrimStart('/','\\');
			}
		}
		/// <summary>
		/// File output path
		/// </summary>
		public String Path
		{
			get
			{
				return m_Path;
			}
			set
			{
				m_Path = value.TrimEnd('\\','/');
			}
		}

		/// <summary>
		/// Output image type
		/// </summary>
		public GraphvizImageType ImageType
		{
			get
			{
				return m_ImageType;
			}
			set
			{
				m_ImageType = value;
			}
		}

		/// <summary>
		/// Deletes .dot file in the current path
		/// </summary>
		public void CleanDotFiles()
		{
			if (Directory.Exists(Path))
			{
				foreach(String f in Directory.GetFiles(Path,"*.dot"))
				{
					try
					{
						File.Delete(f);
					}
					catch(Exception)
					{}
				}
			}
		}

		private String GetNextFileName()
		{
			return String.Format("{0}_{1}",
				m_Prefix,
				DateTime.Now.ToFileTime()
				);
		}

		/// <summary>
		/// Writes the dot code to the dotFileName and then executes dot on it.
		/// </summary>
		/// <param name="dotCode"></param>
		/// <param name="imageType"></param>
		/// <returns>output file name</returns>
		public String Render(
			String dotCode,
			GraphvizImageType imageType
			)
		{
			ImageType = imageType;
			return Render(dotCode);
		}

		/// <summary>
		/// Writes the dot code to the dotFileName and then executes dot on it.
		/// </summary>
		/// <param name="dotCode"></param>
		/// <returns></returns>
		public String Render(String dotCode)
		{
			if (dotCode == null)
				throw new ArgumentNullException("dotCode");
			// create directory if necessary
			if (!Directory.Exists(Path))
				Directory.CreateDirectory(Path);

			String baseFileName = GetNextFileName();

			String ext = ImageType.ToString().ToLower();
			String fileName = Path + '\\' + baseFileName + ".dot";
			String outputFileName = Path + '\\' + baseFileName + '.'+ ext;

			// outputing to file
			using(FileStream file = File.Open(fileName,FileMode.Create,FileAccess.Write))
			{
				byte[] buffer = Encoding.ASCII.GetBytes(dotCode);
				file.Write(buffer,0,buffer.Length);
				file.Close();
			}

			// create commandline
			String commandLine = String.Format("-T{0} -o{1} {2}",
				ext,
				outputFileName,
				fileName
				);

			lock(typeof(GraphVizdotNet.Dot))
			{
				GraphVizdotNet.Dot.Exec(commandLine);	
			}

			return baseFileName + '.'+ ext;
		}
	}
}
