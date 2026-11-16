import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_ea2f2bc35d5e95fd60d2982e2277bb1d
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonObject.sysTableColumn_inputType.get(mapRow.get("INPUT_TYPE"));

    }
}
