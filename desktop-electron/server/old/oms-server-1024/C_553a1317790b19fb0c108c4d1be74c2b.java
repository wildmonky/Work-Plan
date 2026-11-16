import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_553a1317790b19fb0c108c4d1be74c2b
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonObject.mapJCBType.get(mapRow.get("JTYPE"));

    }
}
