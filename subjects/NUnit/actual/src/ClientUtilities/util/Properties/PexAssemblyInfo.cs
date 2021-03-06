using Microsoft.Pex.Framework.Suppression;
using System.IO;
using Microsoft.Pex.Framework.Instrumentation;
using Microsoft.Pex.Framework.Validation;
using Microsoft.Win32;
using System;

[assembly: PexSuppressUninstrumentedMethodFromType(typeof(Path))]
//[assembly: PexInstrumentAssembly("System.Xml")]
//[assembly: PexInstrumentType(typeof(StreamWriter))]
//[assembly: PexInstrumentType(typeof(TextWriter))]

[assembly: PexInstrumentAssembly("System")]
[assembly: PexSuppressUninstrumentedMethodFromType(typeof(Environment))]
[assembly: PexInstrumentType(typeof(RegistryKey))]
[assembly: PexInstrumentType("mscorlib", "Microsoft.Win32.Win32Native")]
[assembly: PexSuppressUninstrumentedMethodFromType(typeof(File))]
[assembly: PexSuppressUninstrumentedMethodFromType(typeof(DirectoryInfo))]
[assembly: PexSuppressUninstrumentedMethodFromType(typeof(FileSystemInfo))]
[assembly: PexSuppressUninstrumentedMethodFromType("System.Xml.XmlCharType")]
[assembly: PexSuppressUninstrumentedMethodFromType(typeof(StreamWriter))]
