using System.Runtime.InteropServices;

namespace CsRMatrix.Engine;

internal abstract class NativeHandle : SafeHandle
{
    protected NativeHandle() : base(IntPtr.Zero, ownsHandle: true) { }

    protected NativeHandle(IntPtr existing, bool ownsHandle)
        : base(IntPtr.Zero, ownsHandle)
        => SetHandle(existing);

    public override bool IsInvalid => handle == IntPtr.Zero;

    protected override bool ReleaseHandle()
    {
        NativeStdLib.Free(handle);
        return true;
    }
}

internal sealed class RashunalHandle : NativeHandle
{
    internal RashunalHandle() : base() { }

    internal RashunalHandle(IntPtr existing, bool ownsHandle)
        : base(existing, ownsHandle) { }
}

internal sealed class RMatrixHandle : NativeHandle
{
    internal RMatrixHandle() : base() { }

    internal RMatrixHandle(IntPtr existing, bool ownsHandle)
        : base(existing, ownsHandle) { }
}

internal sealed class GaussFactorizationHandle : NativeHandle
{
    internal GaussFactorizationHandle() : base() { }

    internal GaussFactorizationHandle(IntPtr existing, bool ownsHandle)
        : base(existing, ownsHandle) { }
}
