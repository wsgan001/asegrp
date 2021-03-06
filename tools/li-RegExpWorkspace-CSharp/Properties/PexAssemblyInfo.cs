using Microsoft.Pex.Framework.Instrumentation;
using System;
using System.Globalization;
using System.Text;
using Microsoft.Pex.Framework.Suppression;
using System.IO;
using AutomatonToCode;

[assembly: PexInstrumentType("mscorlib", "System.IO.TextWriter+SyncTextWriter")]
[assembly: PexInstrumentType(typeof(NumberFormatInfo))]
[assembly: PexInstrumentAssembly("System")]
[assembly: PexInstrumentType(typeof(ASCIIEncoding))]
[assembly: PexInstrumentType(typeof(EncoderFallback))]
[assembly: PexInstrumentType(typeof(EncoderReplacementFallback))]
[assembly: PexInstrumentType(typeof(DecoderFallback))]
[assembly: PexSuppressUninstrumentedMethodFromType(typeof(Random))]
[assembly: PexSuppressUninstrumentedMethodFromType("System.CurrentSystemTimeZone")]
[assembly: PexSuppressUninstrumentedMethodFromType(typeof(TimeZone))]
[assembly: PexSuppressUninstrumentedMethodFromType("System.DateTimeFormat")]
[assembly: PexSuppressUninstrumentedMethodFromType(typeof(DateTimeFormatInfo))]
[assembly: PexSuppressUninstrumentedMethodFromType(typeof(StreamWriter))]
[assembly: PexSuppressUninstrumentedMethodFromType(typeof(StringWrapper))]
[assembly: PexSuppressUninstrumentedMethodFromType(typeof(RandomString))]
