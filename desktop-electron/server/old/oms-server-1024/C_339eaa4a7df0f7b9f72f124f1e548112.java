import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_339eaa4a7df0f7b9f72f124f1e548112
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonObject.mapJjbStartType.get(mapRow.get("START"));

    }
}
