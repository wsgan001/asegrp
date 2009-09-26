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


using System;
using System.IO;

namespace QuickGraph.Algorithms.Graphviz.Collections
{
	/// <summary>
	/// A collection of elements of type GraphvizLayer
	/// </summary>
	public class GraphvizLayerCollection: System.Collections.CollectionBase
	{
		private String m_Separators;

		/// <summary>
		/// Initializes a new empty instance of the GraphvizLayerCollection class.
		/// </summary>
		public GraphvizLayerCollection()
		{
			m_Separators = ":";
		}

		/// <summary>
		/// Initializes a new instance of the GraphvizLayerCollection class, containing elements
		/// copied from an array.
		/// </summary>
		/// <param name="items">
		/// The array whose elements are to be added to the new GraphvizLayerCollection.
		/// </param>
		public GraphvizLayerCollection(GraphvizLayer[] items)
		{
			m_Separators = ":";
			this.AddRange(items);
		}

		/// <summary>
		/// Initializes a new instance of the GraphvizLayerCollection class, containing elements
		/// copied from another instance of GraphvizLayerCollection
		/// </summary>
		/// <param name="items">
		/// The GraphvizLayerCollection whose elements are to be added to the new GraphvizLayerCollection.
		/// </param>
		public GraphvizLayerCollection(GraphvizLayerCollection items)
		{
			m_Separators = ":";
			this.AddRange(items);
		}

		/// <summary>
		/// Adds the elements of an array to the end of this GraphvizLayerCollection.
		/// </summary>
		/// <param name="items">
		/// The array whose elements are to be added to the end of this GraphvizLayerCollection.
		/// </param>
		public virtual void AddRange(GraphvizLayer[] items)
		{
			foreach (GraphvizLayer item in items)
			{
				this.List.Add(item);
			}
		}

		/// <summary>
		/// Adds the elements of another GraphvizLayerCollection to the end of this GraphvizLayerCollection.
		/// </summary>
		/// <param name="items">
		/// The GraphvizLayerCollection whose elements are to be added to the end of this GraphvizLayerCollection.
		/// </param>
		public virtual void AddRange(GraphvizLayerCollection items)
		{
			foreach (GraphvizLayer item in items)
			{
				this.List.Add(item);
			}
		}

		/// <summary>
		/// Adds an instance of type GraphvizLayer to the end of this GraphvizLayerCollection.
		/// </summary>
		/// <param name="value">
		/// The GraphvizLayer to be added to the end of this GraphvizLayerCollection.
		/// </param>
		public virtual void Add(GraphvizLayer value)
		{
			this.List.Add(value);
		}

		/// <summary>
		/// Determines whether a specfic GraphvizLayer value is in this GraphvizLayerCollection.
		/// </summary>
		/// <param name="value">
		/// The GraphvizLayer value to locate in this GraphvizLayerCollection.
		/// </param>
		/// <returns>
		/// true if value is found in this GraphvizLayerCollection;
		/// false otherwise.
		/// </returns>
		public virtual bool Contains(GraphvizLayer value)
		{
			return this.List.Contains(value);
		}

		/// <summary>
		/// Return the zero-based index of the first occurrence of a specific value
		/// in this GraphvizLayerCollection
		/// </summary>
		/// <param name="value">
		/// The GraphvizLayer value to locate in the GraphvizLayerCollection.
		/// </param>
		/// <returns>
		/// The zero-based index of the first occurrence of the _ELEMENT value if found;
		/// -1 otherwise.
		/// </returns>
		public virtual int IndexOf(GraphvizLayer value)
		{
			return this.List.IndexOf(value);
		}

		/// <summary>
		/// Inserts an element into the GraphvizLayerCollection at the specified index
		/// </summary>
		/// <param name="index">
		/// The index at which the GraphvizLayer is to be inserted.
		/// </param>
		/// <param name="value">
		/// The GraphvizLayer to insert.
		/// </param>
		public virtual void Insert(int index, GraphvizLayer value)
		{
			this.List.Insert(index, value);
		}

		/// <summary>
		/// Gets or sets the GraphvizLayer at the given index in this GraphvizLayerCollection.
		/// </summary>
		public virtual GraphvizLayer this[int index]
		{
			get
			{
				return (GraphvizLayer) this.List[index];
			}
			set
			{
				this.List[index] = value;
			}
		}

		/// <summary>
		/// Removes the first occurrence of a specific GraphvizLayer from this GraphvizLayerCollection.
		/// </summary>
		/// <param name="value">
		/// The GraphvizLayer value to remove from this GraphvizLayerCollection.
		/// </param>
		public virtual void Remove(GraphvizLayer value)
		{
			this.List.Remove(value);
		}


		/// <summary>
		/// 
		/// </summary>
		public String Separators
		{
			get
			{
				return m_Separators;
			}
			set
			{
				if (value == null)
					throw new ArgumentNullException("separator is null");
				if (value.Length == 0)
					throw new ArgumentException("separator is empty");
				m_Separators = value;
			}
		}

		/// <summary>
		/// Converts to dot code
		/// </summary>
		/// <returns></returns>
		public String ToDot()
		{
			if (this.Count == 0)
				return null;

			StringWriter sw = new StringWriter();
			sw.Write("layers=\"");
			bool needComa = false;
			foreach(GraphvizLayer layer in this)
			{
				if (needComa)
					sw.Write(Separators);
				else
					needComa = true;

				sw.Write(layer.Name);
			}
			sw.WriteLine("\";");
			sw.WriteLine("layersep=\"{0}\"",Separators);

			return sw.ToString();
		}

		/// <summary>
		/// Type-specific enumeration class, used by GraphvizLayerCollection.GetEnumerator.
		/// </summary>
		public class Enumerator: System.Collections.IEnumerator
		{
			private System.Collections.IEnumerator wrapped;

			/// <summary>
			/// Builds enumerator
			/// </summary>
			/// <param name="collection"></param>
			public Enumerator(GraphvizLayerCollection collection)
			{
				this.wrapped = ((System.Collections.CollectionBase)collection).GetEnumerator();
			}

			/// <summary>
			/// Current layer
			/// </summary>
			public GraphvizLayer Current
			{
				get
				{
					return (GraphvizLayer) (this.wrapped.Current);
				}
			}

			/// <summary>
			/// 
			/// </summary>
			object System.Collections.IEnumerator.Current
			{
				get
				{
					return (GraphvizLayer) (this.wrapped.Current);
				}
			}

			/// <summary>
			/// 
			/// </summary>
			/// <returns></returns>
			public bool MoveNext()
			{
				return this.wrapped.MoveNext();
			}

			/// <summary>
			/// 
			/// </summary>
			public void Reset()
			{
				this.wrapped.Reset();
			}
		}

		/// <summary>
		/// Returns an enumerator that can iterate through the elements of this GraphvizLayerCollection.
		/// </summary>
		/// <returns>
		/// An object that implements System.Collections.IEnumerator.
		/// </returns>        
		public new virtual GraphvizLayerCollection.Enumerator GetEnumerator()
		{
			return new GraphvizLayerCollection.Enumerator(this);
		}
	}
}
