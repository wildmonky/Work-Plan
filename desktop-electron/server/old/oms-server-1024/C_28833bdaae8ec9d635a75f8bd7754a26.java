import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_28833bdaae8ec9d635a75f8bd7754a26
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonObject.mapdtOrZt.get(mapRow.get("DT_OR_ZT"));

    }
}
