import { actionAsmEdited } from "../actionAsm";
import * as types from "redux/reduxTypes";

describe("ASM actions tests", () => {

   it("should create action when user finished editing ASM in designer and pressed Save", () => {
      const data = {id: 1, name: "new name"};

      expect(actionAsmEdited(data)).toEqual({
         type: types.ASM_EDITED,
         data
      });
   });
});