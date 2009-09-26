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


using System.Reflection;
using System.Runtime.CompilerServices;

//
// Les informations g�n�rales relatives � un assembly d�pendent de 
// l'ensemble d'attributs suivant. Pour modifier les informations
// associ�es � un assembly, changez les valeurs de ces attributs.
//
[assembly: AssemblyTitle("QuickGraph.Exceptions")]
[assembly: AssemblyDescription("Exception related to graph library")]
[assembly: AssemblyConfiguration("")]
[assembly: AssemblyCompany("")]
[assembly: AssemblyProduct("")]
[assembly: AssemblyCopyright("")]
[assembly: AssemblyTrademark("")]
[assembly: AssemblyCulture("")]		

//
// Les informations de version pour un assembly se composent des quatre valeurs suivantes�:
//
//      Version principale
//      Version secondaire 
//      Num�ro de build
//      R�vision
//
// Vous pouvez sp�cifier toutes les valeurs ou indiquer des num�ros de r�vision et de build par d�faut 
// en utilisant '*', comme ci-dessous :

[assembly: AssemblyVersion("2.1.*")]

//
// Pour signer votre assembly, vous devez sp�cifier la cl� � utiliser. Consultez 
// la documentation Microsoft .NET Framework pour plus d'informations sur la signature d'un assembly.
//
// Utilisez les attributs ci-dessous pour contr�ler la cl� utilis�e lors de la signature. 
//
// Remarques�: 
//   (*) Si aucune cl� n'est sp�cifi�e, l'assembly n'est pas sign�.
//   (*) KeyName fait r�f�rence � une cl� install�e dans le fournisseur de
//       services cryptographiques (CSP) de votre ordinateur. KeyFile fait r�f�rence � un fichier qui contient
//       une cl�.
//   (*) Si les valeurs de KeyFile et de KeyName sont sp�cifi�es, le 
//       traitement suivant se produit�:
//       (1) Si KeyName se trouve dans le CSP, la cl� est utilis�e.
//       (2) Si KeyName n'existe pas mais que KeyFile existe, la cl� 
//           de KeyFile est install�e dans le CSP et utilis�e.
//   (*) Pour cr�er KeyFile, vous pouvez utiliser l'utilitaire sn.exe (Strong Name, Nom fort).
//        Lors de la sp�cification de KeyFile, son emplacement doit �tre
//        relatif au r�pertoire de sortie du projet qui est
//       %Project Directory%\obj\<configuration>. Par exemple, si votre KeyFile se trouve
//       dans le r�pertoire du projet, vous devez sp�cifier l'attribut 
//       AssemblyKeyFile sous la forme [assembly: AssemblyKeyFile("..\\..\\mykey.snk")]
//   (*) DelaySign (signature diff�r�e) est une option avanc�e. Pour plus d'informations, consultez la
//       documentation Microsoft .NET Framework.
//
[assembly: AssemblyDelaySign(false)]
[assembly: AssemblyKeyFile("")]
[assembly: AssemblyKeyName("")]
