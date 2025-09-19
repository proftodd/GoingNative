using System.Reflection;
using System.Runtime.InteropServices;

namespace CsRMatrix.Engine;

public static partial class NativeStdLib
{
    static NativeStdLib()
    {
        NativeLibrary.SetDllImportResolver(typeof(NativeStdLib).Assembly, ResolveLib);
    }

    private static IntPtr ResolveLib(string libraryName, Assembly assembly, DllImportSearchPath? searchPath)
    {
        if (libraryName == "c")
        {
            if (RuntimeInformation.IsOSPlatform(OSPlatform.Windows))
                return NativeLibrary.Load("ucrtbase.dll", assembly, searchPath);
            if (RuntimeInformation.IsOSPlatform(OSPlatform.Linux))
                return NativeLibrary.Load("libc.so.6", assembly, searchPath);
            if (RuntimeInformation.IsOSPlatform(OSPlatform.OSX))
                return NativeLibrary.Load("libSystem.dylib", assembly, searchPath);
        }
        return IntPtr.Zero;
    }

    [LibraryImport("c", EntryPoint = "free")]
    internal static partial void Free(IntPtr ptr);
}
