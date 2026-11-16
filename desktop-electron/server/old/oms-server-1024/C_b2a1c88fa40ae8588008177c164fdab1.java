import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_b2a1c88fa40ae8588008177c164fdab1
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonObject.sysTable_InputType.get(mapRow.get("INPUT_TYPE"));

    }
}
