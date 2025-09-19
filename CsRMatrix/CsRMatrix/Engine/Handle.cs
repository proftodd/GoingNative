using System.Runtime.InteropServices;

namespace CsRMatrix.Engine;

internal sealed class RashunalHandle : SafeHandle
{
    private RashunalHandle() : base(IntPtr.Zero, ownsHandle: true) { }

    internal RashunalHandle(IntPtr existing, bool ownsHandle)
        : base(IntPtr.Zero, ownsHandle)
        => SetHandle(existing);

    public override bool IsInvalid => handle == IntPtr.Zero;

    protected override bool ReleaseHandle()
    {
        NativeStdLib.Free(handle);
        return true;
    }
}

internal sealed class RMatrixHandle : SafeHandle
{
    private RMatrixHandle() : base(IntPtr.Zero, ownsHandle: true) { }

    internal RMatrixHandle(IntPtr existing, bool ownsHandle)
        : base(IntPtr.Zero, ownsHandle)
        => SetHandle(existing);

    public override bool IsInvalid => handle == IntPtr.Zero;

    protected override bool ReleaseHandle()
    {
        NativeStdLib.Free(handle);
        return true;
    }
}

internal sealed class GaussFactorizationHandle : SafeHandle
{
    private GaussFactorizationHandle() : base(IntPtr.Zero, ownsHandle: true) { }

    internal GaussFactorizationHandle(IntPtr existing, bool ownsHandle)
        : base(IntPtr.Zero, ownsHandle)
        => SetHandle(existing);

    public override bool IsInvalid => handle == IntPtr.Zero;

    protected override bool ReleaseHandle()
    {
        NativeStdLib.Free(handle);
        return true;
    }
}
