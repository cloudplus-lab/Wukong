package com.github.wukong.azurevm.models.virtualMachineExtensions;

public class DeleteWithServiceResponseAsyncRequest implements com.github.wukong.azurevm.models.AbstractAzureRequest {
	protected java.lang.String resourceGroupName;

	protected java.lang.String vmName;

	protected java.lang.String vmExtensionName;

	public void setResourceGroupName(java.lang.String resourceGroupName) {
		this.resourceGroupName = resourceGroupName;
}
	public java.lang.String getResourceGroupName() {
		return this.resourceGroupName;
}
	public void setVmName(java.lang.String vmName) {
		this.vmName = vmName;
}
	public java.lang.String getVmName() {
		return this.vmName;
}
	public void setVmExtensionName(java.lang.String vmExtensionName) {
		this.vmExtensionName = vmExtensionName;
}
	public java.lang.String getVmExtensionName() {
		return this.vmExtensionName;
}
}