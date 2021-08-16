import { createCapabilityApi } from "./capability";

it("should fetch", async () => {
  const capabilityApi = createCapabilityApi({
    baseURL: "http://localhost:8080",
    auth: {
      username: "admin",
      password: "admin",
    },
  });

  const response = await capabilityApi.find({});
  // eslint-disable-next-line no-console
  console.log("response", response.data);
  expect(response.data).toBeDefined();
});
